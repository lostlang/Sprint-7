package com.example.demo

import com.example.demo.controller.Controller
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(Controller::class)
class ControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @ParameterizedTest
    @MethodSource("different data pages")
    fun correct(inp_string: String) {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.get("/$inp_string")
        )

        result.andExpect(MockMvcResultMatchers.status().isOk)
              .andExpect(MockMvcResultMatchers.content().string(inp_string))
    }

    companion object {
        @JvmStatic
        fun `different data pages`() = listOf(
            "test",
            "42",
            "main_page"
        )
    }

}