package com.proyectoexamen.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class BackendApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() { }

    @Test
    void mainPagesRenderSuccessfully() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk());
        mockMvc.perform(get("/productos")).andExpect(status().isOk());
        mockMvc.perform(get("/productos/nuevo")).andExpect(status().isOk());
        mockMvc.perform(get("/categorias")).andExpect(status().isOk());
        mockMvc.perform(get("/categorias/nueva")).andExpect(status().isOk());
        mockMvc.perform(get("/proveedores")).andExpect(status().isOk());
        mockMvc.perform(get("/proveedores/nuevo")).andExpect(status().isOk());
        mockMvc.perform(get("/ventas/nueva")).andExpect(status().isOk());
        mockMvc.perform(get("/media/productos/demo-lapicero.svg")).andExpect(status().isOk());
        mockMvc.perform(get("/compras/nueva")).andExpect(status().isOk());
        mockMvc.perform(get("/inventario/movimientos")).andExpect(status().isOk());
        mockMvc.perform(get("/inventario/ajuste")).andExpect(status().isOk());
    }
}
