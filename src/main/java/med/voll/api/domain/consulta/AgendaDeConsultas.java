package med.voll.api.domain.consulta;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import med.voll.api.domain.ValidacaoException;
import med.voll.api.domain.medico.Medico;
import med.voll.api.domain.medico.MedicoRepository;
import med.voll.api.domain.paciente.PacienteRepository;

@Service
public class AgendaDeConsultas {
	
	@Autowired
	private ConsultaRepository consultaRepository;
	
	@Autowired
	private MedicoRepository medicoRepository;
	
	@Autowired
	private PacienteRepository pacienteRepository;
	
	
	public void agendar(@Valid DadosAgendamentoConsulta dados) {
		if(!pacienteRepository.existsById(dados.idPaciente())) {
			throw new ValidacaoException("Id informado nao existe");
		}
		
		if(dados.idMedico() != null && !medicoRepository.existsById(dados.idMedico())) {
			throw new ValidacaoException("Id informado nao existe");
		}
		
		var paciente = pacienteRepository.findById(dados.idPaciente()).get();
//		var medico = medicoRepository.findById(dados.idMedico()).get();
		var medico = escolherMedico(dados);
		var consulta = new Consulta(null, medico, paciente, dados.data());
		consultaRepository.save(consulta);
	}


	private Medico escolherMedico(@Valid DadosAgendamentoConsulta dados) {
		if(dados.idMedico() != null) {
			return medicoRepository.getReferenceById(dados.idMedico());
		}
		if(dados.especialidade() == null) {
			throw new ValidacaoException("Especialidade e obrigatoria quando medico nao e escolhido");
		}
		return medicoRepository.escolherMedicoAleatorioLivreNaData(dados.especialidade(), dados.data());
	}
}
