package org.padler.thydbadmin.controller;

import org.padler.thydbadmin.service.DbAdminService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ConditionalOnProperty("thyDbAdmin.controller")
@Controller
@RequestMapping("/thyDbAdmin")
public class ThyDbAdminController {

    public static final String COLUMNS = "columns";
    private final DbAdminService dbAdminService;

    public ThyDbAdminController(DbAdminService dbAdminService) {
        this.dbAdminService = dbAdminService;
    }

    @GetMapping("")
    public String tables(Model model) {
        List<String> tables = dbAdminService.getTables();
        model.addAttribute("tables", tables);
        return "tables";
    }

    @GetMapping("/table/{tableName}")
    public String table(@PathVariable String tableName, @RequestParam(required = false, defaultValue = "0") Integer page,
                          @RequestParam(required = false, defaultValue = "10") Integer pageSize, Model model) {
        List<String> columns = dbAdminService.getColumns(tableName);
        Page<Object[]> result = dbAdminService.getData(tableName, page, pageSize);
        model.addAttribute("tableName", tableName);
        model.addAttribute(COLUMNS, columns);
        model.addAttribute("rows", result.getContent());
        model.addAttribute("page", page);
        model.addAttribute("pages", result.getTotalPages());
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("total", result.getTotalElements());
        return COLUMNS;
    }

    @GetMapping("/queryResult")
    public String queryResult() {
        return "queryResult";
    }

    @PostMapping("/executeQuery")
    public String tables(String query, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String referer = request.getHeader("Referer");

        List<Map<String, Object>> list = null;
        try {
            list = dbAdminService.executeQuery(query);
        } catch (PersistenceException e) {
            redirectAttributes.addFlashAttribute("error", e.getCause().getCause().getMessage());
        }

        if (list == null) {
            return "redirect:" + referer;
        }

        ArrayList<String> columns = null;
        if (!list.isEmpty())
            columns = new ArrayList<>(list.get(0).keySet());
        redirectAttributes.addFlashAttribute(COLUMNS, columns);
        redirectAttributes.addFlashAttribute("data", list);
        return "redirect:" + "queryResult";
    }

}
