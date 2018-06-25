package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("menu")
public class MenuController {

    @Autowired
    private CheeseDao cheeseDao;
    @Autowired
    private MenuDao menuDao;

    @RequestMapping(value = "")
    private String index (Model model) {
        model.addAttribute("title", "List of Menus");
        model.addAttribute("menus", menuDao.findAll());
        return "menu/index";
    }

    @RequestMapping(value="add", method = RequestMethod.GET)
    private String add(Model model, Menu menu) {
        model.addAttribute("title", "Add Menu");
        model.addAttribute(new Menu());
        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    private String add(Model model, @ModelAttribute @Valid Menu menus, Errors errors) {
        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Menu");
            return "add";
        }
        menuDao.save(menus);
        return "redirect:view/" + menus.getId();

    }

    @RequestMapping(value="view/{menuId}", method = RequestMethod.GET)
    private String viewMenu(Model model, @PathVariable int menuId) {
        Menu menu = menuDao.findOne(menuId);
        model.addAttribute("title", menu.getName());
        model.addAttribute("menu", menu);
        return "menu/view";
    }

    @RequestMapping(value = "add-item/{menuId}", method = RequestMethod.GET)
    private String addItem(Model model, @PathVariable int menuId) {
        Menu menu = menuDao.findOne(menuId);

        AddMenuItemForm form = new AddMenuItemForm(menu, cheeseDao.findAll());

        model.addAttribute("form",form);
        model.addAttribute("title", "Add item to menu:" + menu.getName());
        return "menu/add-item";
    }

    @RequestMapping(value = "add-item", method = RequestMethod.POST)
    private String addItem(Model model, @ModelAttribute @Valid AddMenuItemForm form, int menuId, int cheeseId, Errors errors) {
        if (errors.hasErrors()) {
            model.addAttribute("form", form);
            return "menu/add-item";
        }
        Menu aMenu = menuDao.findOne(menuId);

        Cheese aCheese = cheeseDao.findOne(cheeseId);

        aMenu.addItem(aCheese);

        // this makes the change in the database
        menuDao.save(aMenu);

        return "redirect:/menu/view/" + aMenu.getId();
    }
}
