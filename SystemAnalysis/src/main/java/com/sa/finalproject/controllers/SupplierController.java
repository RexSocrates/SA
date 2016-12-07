package com.sa.finalproject.controllers;

import java.util.ArrayList;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.sa.finalproject.DAO.impl.SupplierDAOImpl;
import com.sa.finalproject.entity.Supplier;

@Controller
public class SupplierController {
	ApplicationContext context = new ClassPathXmlApplicationContext("spring-module.xml");
	
	
	@RequestMapping(value = "/SupplierList", method = RequestMethod.GET)
	public ModelAndView listSupplier(){
		// 開立請購單
		ModelAndView model = new ModelAndView("SupplierList");
		SupplierDAOImpl supplierDAO = (SupplierDAOImpl)context.getBean("supplierDAO");
		ArrayList<Supplier> supplierList = new ArrayList<Supplier>();
		supplierList = supplierDAO.getList();
		model.addObject("supplierList", supplierList);
		
		return model;
	}
	
	
	@RequestMapping(value = "/newSupplier", method = RequestMethod.POST)
	public ModelAndView addSupplier(@ModelAttribute("supplierName")String supplierName, @ModelAttribute("supplierPhone")String supplierPhone, @ModelAttribute("supplierAddress")String supplierAddress){
		// Add the supplier information
		ModelAndView model = new ModelAndView("redirect:/SupplierList");
		SupplierDAOImpl supplierDAO = (SupplierDAOImpl)context.getBean("supplierDAO");
		Supplier newSupplier = new Supplier();
		
		newSupplier.setSupplierName(supplierName);
		newSupplier.setPhone(supplierPhone);
		newSupplier.setAddress(supplierAddress);
		System.out.println(newSupplier.getSupplierName());
		supplierDAO.insert(newSupplier);
		
		
		return model;
	}
	
	
	@RequestMapping(value = "/remarkedSupplierList", method = RequestMethod.GET)
	public ModelAndView listRemarkedSupplier(){
		// 開立請購單
		ModelAndView model = new ModelAndView("SupplierListDetails");
		SupplierDAOImpl supplierDAO = (SupplierDAOImpl)context.getBean("supplierDAO");
		
		
		return model;
	}
}