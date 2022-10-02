package com.bootcamp.dsclient.services;

import com.bootcamp.dsclient.dto.ClientDTO;
import com.bootcamp.dsclient.entities.Client;
import com.bootcamp.dsclient.repository.ClientRepository;
import com.bootcamp.dsclient.services.exceptions.DataBaseException;
import com.bootcamp.dsclient.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Transactional(readOnly = true)
    public Page<ClientDTO> findAllPaged(PageRequest pageRequest) {
        Page<Client> list =clientRepository.findAll(pageRequest);

        return list.map(client -> new ClientDTO(client));
    }

    @Transactional(readOnly = true)
    public ClientDTO findById(Long id){
        Optional<Client> obj = clientRepository.findById(id);
        Client entity = obj.orElseThrow(()-> new ResourceNotFoundException("Objeto nao encontrado"));
        return new ClientDTO(entity);
    }

    @Transactional
    public ClientDTO save(ClientDTO dto){
        Client obj = new Client();
        copyDtoToEntity(dto, obj);
        return new ClientDTO(clientRepository.save(obj));
    }

    @Transactional
    public ClientDTO update(ClientDTO dto){
        try {
            Optional<Client> entity = clientRepository.findById(dto.getId());
            copyDtoToEntity(dto, entity.get());
            return new ClientDTO(clientRepository.save(entity.get()));
        } catch (NoSuchElementException e){
            throw new ResourceNotFoundException("Entity not found");
        }
    }

    public void delete(Long id){
        try {
            clientRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e){
            throw new ResourceNotFoundException("Entity not found");
        } catch (DataIntegrityViolationException e) {
            throw new DataBaseException("Integraty violation");
        }
    }

    public void copyDtoToEntity(ClientDTO dto, Client entity){
        entity.setName(dto.getName());
        entity.setIncome(dto.getIncome());
        entity.setBirthDate(dto.getBirthDate());
        entity.setCpf(dto.getCpf());
        entity.setChildren(dto.getChildren());
    }
}
