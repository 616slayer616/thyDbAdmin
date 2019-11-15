package org.padler.thydbadmin;


import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint.NONE;

@AutoConfigureMockMvc(print = NONE)
@org.springframework.boot.test.context.SpringBootTest(classes = TestApplication.class, webEnvironment = org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractSpringBootTest {
}
