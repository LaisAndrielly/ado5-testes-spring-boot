package br.senac.sp.tads.dsw.exemplo5.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import br.senac.sp.tads.dsw.exemplo5.model.Departamento;
import br.senac.sp.tads.dsw.exemplo5.model.Funcionario;
import br.senac.sp.tads.dsw.exemplo5.repository.DepartamentoRepository;
import br.senac.sp.tads.dsw.exemplo5.repository.FuncionarioRepository;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional 
public class FuncionarioControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Departamento departamentoPadrao;

    @BeforeEach
    void setUp() {
        Departamento departamento = new Departamento();
        departamento.setNome("Desenvolvimento");
        departamento.setOrcamento(200000.00);
        departamentoPadrao = departamentoRepository.save(departamento);
    }

    @Test
    void deveCriarFuncionarioComSucesso() throws Exception {
Funcionario funcionario = new Funcionario();
funcionario.setNome("João Silva");
funcionario.setDataContratacao(
        java.time.LocalDate.of(2024, 5, 15));
funcionario.setTrabalhoRemoto(true);
funcionario.setDepartamento(departamentoPadrao);

String json = objectMapper.writeValueAsString(funcionario);

mockMvc.perform(post("/api/funcionarios")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.nome").value("João Silva"))
        .andExpect(jsonPath("$.trabalhoRemoto").value(true));
    }

    @Test
    void deveRetornarErro400AoCriarFuncionarioComDataFutura() throws Exception {
Funcionario funcionario = new Funcionario();
funcionario.setNome("Maria Silva");
funcionario.setDataContratacao(
        java.time.LocalDate.of(2050, 1, 1));
funcionario.setTrabalhoRemoto(true);
funcionario.setDepartamento(departamentoPadrao);

String json = objectMapper.writeValueAsString(funcionario);

mockMvc.perform(post("/api/funcionarios")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
        .andExpect(status().isBadRequest());
    }

    @Test
    void deveListarTodosOsFuncionarios() throws Exception {
Funcionario funcionario = new Funcionario();
funcionario.setNome("Carlos Souza");
funcionario.setDataContratacao(
        java.time.LocalDate.of(2024, 3, 10));
funcionario.setTrabalhoRemoto(false);
funcionario.setDepartamento(departamentoPadrao);

funcionarioRepository.save(funcionario);

mockMvc.perform(get("/api/funcionarios"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].nome").value("Carlos Souza"));
    }

    @Test
    void deveBuscarFuncionarioPorId() throws Exception {
Funcionario funcionario = new Funcionario();
funcionario.setNome("Ana Oliveira");
funcionario.setDataContratacao(
        java.time.LocalDate.of(2024, 6, 10));
funcionario.setTrabalhoRemoto(true);
funcionario.setDepartamento(departamentoPadrao);

Funcionario funcionarioSalvo = funcionarioRepository.save(funcionario);

mockMvc.perform(get("/api/funcionarios/" + funcionarioSalvo.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(funcionarioSalvo.getId()))
        .andExpect(jsonPath("$.nome").value("Ana Oliveira"));
    }

    @Test
    void deveAtualizarFuncionario() throws Exception {
Funcionario funcionario = new Funcionario();
funcionario.setNome("Pedro Santos");
funcionario.setDataContratacao(
        java.time.LocalDate.of(2024, 2, 10));
funcionario.setTrabalhoRemoto(false);
funcionario.setDepartamento(departamentoPadrao);

Funcionario funcionarioSalvo = funcionarioRepository.save(funcionario);

funcionarioSalvo.setNome("Pedro Atualizado");
funcionarioSalvo.setTrabalhoRemoto(true);

String json = objectMapper.writeValueAsString(funcionarioSalvo);

mockMvc.perform(put("/api/funcionarios/" + funcionarioSalvo.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nome").value("Pedro Atualizado"))
        .andExpect(jsonPath("$.trabalhoRemoto").value(true));
    }

    @Test 
    void deveApagarFuncionario() throws Exception {
Funcionario funcionario = new Funcionario();
funcionario.setNome("Lucas Ferreira");
funcionario.setDataContratacao(
        java.time.LocalDate.of(2024, 4, 10));
funcionario.setTrabalhoRemoto(false);
funcionario.setDepartamento(departamentoPadrao);

Funcionario funcionarioSalvo = funcionarioRepository.save(funcionario);

mockMvc.perform(delete("/api/funcionarios/" + funcionarioSalvo.getId()))
        .andExpect(status().isNoContent());
    }
}
