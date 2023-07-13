package org.padler.thydbadmin.controller;

import org.padler.thydbadmin.service.DbAdminService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ConditionalOnProperty("thyDbAdmin.controller")
@Controller
@RequestMapping("/thyDbAdmin")
public class ThyDbAdminController {

    public static final String COLUMNS = "columns";
    public static final String TABLE = "table";
    public static final String PAGE = "page";
    public static final String PAGES = "pages";
    public static final String PAGE_SIZE = "pageSize";
    public static final String ROWS = "rows";
    public static final String QUERY = "query";
    public static final String TOTAL = "total";
    private final DbAdminService dbAdminService;

    public ThyDbAdminController(DbAdminService dbAdminService) {
        this.dbAdminService = dbAdminService;
    }

    @GetMapping("")
    public String getTables(Model model) {
        List<String> tables = dbAdminService.getTables();
        model.addAttribute("tables", tables);
        return "tables";
    }

    @GetMapping("/table/{tableName}")
    public String getTable(@PathVariable String tableName, @RequestParam(required = false, defaultValue = "0") Integer page,
                           @RequestParam(required = false, defaultValue = "10") Integer pageSize, Model model) {
        Page<Map<String, Object>> result = dbAdminService.getData(tableName, page, pageSize);
        model.addAttribute("tableName", tableName);

        if (result.isEmpty()) {
            List<String> columns = dbAdminService.getColumns(tableName);
            model.addAttribute(COLUMNS, columns);
        } else {
            model.addAttribute(COLUMNS, getColumnNames(result));
        }

        model.addAttribute(ROWS, result.getContent());
        model.addAttribute(PAGE, page);
        model.addAttribute(PAGES, result.getTotalPages());
        model.addAttribute(PAGE_SIZE, pageSize);
        model.addAttribute(TOTAL, result.getTotalElements());
        return TABLE;
    }

    @GetMapping("/info")
    public String getInfo(Model model) {
        DatabaseMetaData info = dbAdminService.getInfo();
        model.addAttribute("info", info);
        return "info";
    }

    @GetMapping("/queryResult")
    public String getQueryResult() {
        return "queryResult";
    }

    @PostMapping("/executeQuery")
    public String postExecuteQuery(String query, HttpServletRequest request, RedirectAttributes redirectAttributes,
                                   @RequestParam(required = false, defaultValue = "0") int page,
                                   @RequestParam(required = false, defaultValue = "10") int pageSize) {
        String referer = request.getHeader("Referer");

        Page<Map<String, Object>> result = null;
        try {
            result = dbAdminService.executeQuery(query, page, pageSize);
        } catch (PersistenceException e) {
            redirectAttributes.addFlashAttribute("error", e.getCause().getCause().getMessage());
        }

        if (result == null) {
            return "redirect:" + referer;
        }

        redirectAttributes.addFlashAttribute(COLUMNS, getColumnNames(result));
        redirectAttributes.addFlashAttribute(ROWS, result.getContent());
        redirectAttributes.addFlashAttribute(PAGE, page);
        redirectAttributes.addFlashAttribute(PAGES, result.getTotalPages());
        redirectAttributes.addFlashAttribute(PAGE_SIZE, pageSize);
        redirectAttributes.addFlashAttribute(QUERY, query);
        redirectAttributes.addFlashAttribute(TOTAL, result.getTotalElements());
        return "redirect:" + "queryResult";
    }

    private ArrayList<String> getColumnNames(Page<Map<String, Object>> result) {
        List<Collection<Object>> list = result.getContent().stream()
                .map(Map::values).collect(Collectors.toList());
        ArrayList<String> columns = null;
        if (!list.isEmpty())
            columns = new ArrayList<>(result.getContent().get(0).keySet());

        return columns;
    }

}
