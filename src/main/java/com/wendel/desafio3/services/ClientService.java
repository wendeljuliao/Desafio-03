package com.wendel.desafio3.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.wendel.desafio3.dtos.ClientDTO;
import com.wendel.desafio3.entities.Client;
import com.wendel.desafio3.repositories.ClientRepository;
import com.wendel.desafio3.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ClientService {

	@Autowired
	private ClientRepository clientRepository;

	@Transactional(readOnly = true)
	public Page<ClientDTO> findAll(Pageable pageable) {
		var listClients = clientRepository.findAll(pageable);
		return listClients.map(x -> new ClientDTO(x));
	}

	@Transactional(readOnly = true)
	public ClientDTO findById(Long id) {
		Client foundClient = clientRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado."));
		return new ClientDTO(foundClient);
	}

	@Transactional
	public ClientDTO insert(ClientDTO clientDTO) {
		Client newClient = new Client();
		copyDtoToEntity(clientDTO, newClient);
		newClient = clientRepository.save(newClient);

		return new ClientDTO(newClient);
	}

	@Transactional
	public ClientDTO update(Long id, ClientDTO clientDTO) {
		try {
			Client client = this.clientRepository.getReferenceById(id);
			copyDtoToEntity(clientDTO, client);
			client = this.clientRepository.save(client);
			return new ClientDTO(client);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Cliente não encontrado.");
		}
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	public void delete(Long id) {
		if (!clientRepository.existsById(id)) {
			throw new ResourceNotFoundException("Cliente não encontrado.");
		}

		clientRepository.deleteById(id);
	}

	private void copyDtoToEntity(ClientDTO clientDTO, Client entity) {
		entity.setName(clientDTO.getName());
		entity.setCpf(clientDTO.getCpf());
		entity.setIncome(clientDTO.getIncome());
		entity.setBirthDate(clientDTO.getBirthDate());
		entity.setChildren(clientDTO.getChildren());
	}

}
