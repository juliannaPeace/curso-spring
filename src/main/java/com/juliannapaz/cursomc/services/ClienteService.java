package com.juliannapaz.cursomc.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.juliannapaz.cursomc.domain.Cidade;
import com.juliannapaz.cursomc.domain.Cliente;
import com.juliannapaz.cursomc.domain.Endereco;
import com.juliannapaz.cursomc.domain.enums.TipoCliente;
import com.juliannapaz.cursomc.dto.ClienteDTO;
import com.juliannapaz.cursomc.dto.ClienteNewDTO;
import com.juliannapaz.cursomc.exceptions.DataIntegrityException;
import com.juliannapaz.cursomc.exceptions.ObjectNotFoundException;
import com.juliannapaz.cursomc.repositories.CidadeRepository;
import com.juliannapaz.cursomc.repositories.ClienteRepository;
import com.juliannapaz.cursomc.repositories.EnderecoRepository;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private CidadeRepository cidadeRepository;
	
	@Autowired
	private EnderecoRepository enderecoRepository;

	public Cliente find(Integer id) throws ObjectNotFoundException {

		Optional<Cliente> cliente = clienteRepository.findById(id);

		return cliente.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ",tipo:" + Cliente.class.getName()));
	}

	@Transactional
	public Cliente insert(Cliente cliente) {
		cliente.setId(null);
		cliente = clienteRepository.save(cliente);
		
		enderecoRepository.saveAll(cliente.getEnderecos());
		return cliente;
	}

	public Cliente update(Cliente cliente) {
		Cliente clientePersist = find(cliente.getId());
		updateData(clientePersist, cliente);
		return clienteRepository.save(clientePersist);
	}

	public void delete(Integer id) {
		find(id);

		try {
			clienteRepository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Cliente não pode ser removido, pois o mesmo possui pedidos.");
		}
	}

	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {

		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);

		return clienteRepository.findAll(pageRequest);
	}

	public Cliente fromDTO(ClienteDTO clienteDTO) {
		return new Cliente(clienteDTO.getId(), clienteDTO.getNome(), clienteDTO.getEmail(), null, null);
	}

	public Cliente fromDTO(ClienteNewDTO clienteNewDTO) {
		
		Cliente cliente = new Cliente(null, clienteNewDTO.getNome(), clienteNewDTO.getEmail(),
				clienteNewDTO.getCpfOuCnpj(), TipoCliente.toEnum(clienteNewDTO.getTipoCliente()));
		
		Optional<Cidade> cidade = cidadeRepository.findById(clienteNewDTO.getCidadeId());
		
		Endereco endereco = new Endereco(null,clienteNewDTO.getLogradouro(),clienteNewDTO.getNumero(),
				clienteNewDTO.getComplemento(),clienteNewDTO.getBairro(),clienteNewDTO.getCep(),cliente,cidade.get());
		
		cliente.getEnderecos().add(endereco);
		cliente.getTelefones().add(clienteNewDTO.getTelefone1());
		
		if(clienteNewDTO.getTelefone2()!=null) {
			cliente.getTelefones().add(clienteNewDTO.getTelefone2());
		}
		
		if(clienteNewDTO.getTelefone3()!=null) {
			cliente.getTelefones().add(clienteNewDTO.getTelefone3());
		}
		
		return cliente;
	}

	private void updateData(Cliente clientePersist, Cliente novoCliente) {
		clientePersist.setNome(novoCliente.getNome());
		clientePersist.setEmail(novoCliente.getEmail());
	}

}
