package com.devsuperior.bds04.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.bds04.dto.CityDTO;
import com.devsuperior.bds04.dto.EventDTO;
import com.devsuperior.bds04.entities.City;
import com.devsuperior.bds04.entities.Event;
import com.devsuperior.bds04.repositories.CityRepository;
import com.devsuperior.bds04.repositories.EventRepository;
import com.devsuperior.bds04.services.exceptions.DatabaseException;
import com.devsuperior.bds04.services.exceptions.ResourceNotFoundException;

@Service
public class CityService {
	
	@Autowired
	private CityRepository repository;
	
	@Autowired
	private EventRepository eventRepository;
	
	public Page<CityDTO> findAllPaged(Pageable pageable) {
		Page<City> list = repository.findAll(pageable);
		return list.map(x -> new CityDTO(x));		
	}
	
	public List<CityDTO> findAll() {
		List<City> list = repository.findAll(Sort.by("name"));
		return list.stream().map(x -> new CityDTO(x)).collect(Collectors.toList());	
		
	}
	
	
	
	@Transactional
	public CityDTO insert(CityDTO dto) {
		City entity = new City();
		copyDtoToEntity(dto, entity);		
		entity = repository.save(entity);
		return new CityDTO(entity);
	}
	
	private void copyDtoToEntity(CityDTO dto, City entity) {
		entity.setName(dto.getName());
		
		entity.getEvents().clear();
		
		for(EventDTO eveDTO : dto.getEvents()) {
			Event event  = eventRepository.getOne(eveDTO.getId());
			entity.getEvents().add(event);
		}
		
		
		
	}

	public void delete(Long id) {
		try {
			repository.deleteById(id);
		}catch(EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id not found" + id);	
		}catch(DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity violation");
		}
		
		
	}
	

}
