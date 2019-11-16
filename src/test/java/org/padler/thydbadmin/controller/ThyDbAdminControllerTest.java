package org.padler.thydbadmin.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.padler.thydbadmin.AbstractSpringBootTest;
import org.padler.thydbadmin.service.DbAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.PersistenceException;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ThyDbAdminControllerTest extends AbstractSpringBootTest {

    @Autowired
    protected MockMvc mockMvc;

    @Mock
    private DbAdminService mockDbAdminService;

    @Autowired
    private ThyDbAdminController thyDbAdminController;

    @BeforeEach
    void setup() {
        setField(thyDbAdminController, "dbAdminService", mockDbAdminService);
    }

    @Test
    void tables() throws Exception {
        List<String> tables = new ArrayList<>();
        tables.add("TEST");
        doReturn(tables).when(mockDbAdminService).getTables();

        mockMvc.perform(get("/thyDbAdmin/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("TEST")));

    }

    @Test
    void columns() throws Exception {
        List<String> columns = new ArrayList<>();
        columns.add("TEST_COLUMN");
        doReturn(columns).when(mockDbAdminService).getColumns("TEST_COLUMN");
        Page<Object[]> queryResult = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        doReturn(queryResult).when(mockDbAdminService).getData(eq("TEST_COLUMN"), anyInt(), anyInt());

        mockMvc.perform(get("/thyDbAdmin/table/TEST_COLUMN"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("TEST_COLUMN")));
    }

    @Test
    void info() throws Exception {
        DatabaseMetaData databseMetaData = mock(DatabaseMetaData.class);
        doReturn(databseMetaData).when(mockDbAdminService).getInfo();

        mockMvc.perform(get("/thyDbAdmin/info"))
                .andExpect(status().isOk());
    }

    @Test
    void queryResult() throws Exception {
        mockMvc.perform(get("/thyDbAdmin/queryResult"))
                .andExpect(status().isOk());
    }

    @Test
    void executeQuery_SELECT() throws Exception {
        Map<String, Object> resultMap = Collections.singletonMap("col", "data");
        Page<Map<String, Object>> result = new PageImpl<>(Collections.singletonList(resultMap), PageRequest.of(0, 10), 0);
        doReturn(result).when(mockDbAdminService).executeQuery(eq("SELECT * FROM USERS"), anyInt(), anyInt());

        mockMvc.perform(post("/thyDbAdmin/executeQuery")
                .param("query", "SELECT * FROM USERS"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("queryResult"));
    }

    @Test
    void executeQuery_SELECT_empty() throws Exception {
        Page<Map<String, Object>> result = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        doReturn(result).when(mockDbAdminService).executeQuery(eq("SELECT * FROM USERS"), anyInt(), anyInt());

        mockMvc.perform(post("/thyDbAdmin/executeQuery")
                .param("query", "SELECT * FROM USERS"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("queryResult"));
    }

    @Test
    void executeQuery_UPDATE() throws Exception {
        doReturn(null).when(mockDbAdminService).executeQuery(eq("UPDATE"), anyInt(), anyInt());

        mockMvc.perform(post("/thyDbAdmin/executeQuery")
                .param("query", "UPDATE")
                .header("referer", "thyDbAdmin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("thyDbAdmin"));
    }

    @Test
    void executeQuery_UPDATE_FAIL() throws Exception {
        Throwable cause1 = new Throwable();
        Throwable cause2 = new Throwable(cause1);
        PersistenceException persistenceException = new PersistenceException(cause2);
        doThrow(persistenceException).when(mockDbAdminService).executeQuery(eq("UPDATE"), anyInt(), anyInt());

        mockMvc.perform(post("/thyDbAdmin/executeQuery")
                .param("query", "UPDATE")
                .header("referer", "thyDbAdmin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("thyDbAdmin"));
    }
}
