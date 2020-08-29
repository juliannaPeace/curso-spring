package com.juliannapaz.cursomc.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.juliannapaz.cursomc.domain.Categoria;
import com.juliannapaz.cursomc.dto.CategoriaDTO;
import com.juliannapaz.cursomc.exceptions.DataIntegrityException;
import com.juliannapaz.cursomc.exceptions.ObjectNotFoundException;
import com.juliannapaz.cursomc.repositories.CategoriaRepository;

@Service
public class CategoriaService {

	@Autowired
	private CategoriaRepository categoriaRepository;

	public Categoria find(Integer id) throws ObjectNotFoundException {

		Optional<Categoria> categoria = categoriaRepository.findById(id);

		return categoria.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ",tipo:" + Categoria.class.getName()));
	}

	public Categoria insert(Categoria categoria) {
		categoria.setId(null);
		return categoriaRepository.save(categoria);
	}

	
	public Categoria update(Categoria categoria) {
		Categoria categoriaPersist = find(categoria.getId());
		updateData(categoriaPersist, categoria);
		return categoriaRepository.save(categoriaPersist);
	}
	
	public void delete(Integer id) {
		find(id);
		try {
			categoriaRepository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Categoria não pode ser removida, pois a mesma possui produtos.");
		}
	}

	public List<Categoria> findAll() {
		return categoriaRepository.findAll();
	}

	public Page<Categoria> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {

		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);

		return categoriaRepository.findAll(pageRequest);
	}

	public Categoria fromDTO(CategoriaDTO categoriaDTO) {

		return new Categoria(categoriaDTO.getId(), categoriaDTO.getNome());

	}
	
	private void updateData(Categoria categoriaPersist, Categoria categoriaNova) {
		categoriaPersist.setNome(categoriaNova.getNome());
	}
}
