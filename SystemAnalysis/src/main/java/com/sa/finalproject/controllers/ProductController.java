package com.sa.finalproject.controllers;


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

import com.sa.finalproject.entity.Product;
import com.sa.finalproject.entity.Supplier;
import com.sa.finalproject.DAO.ProductDAO;
import com.sa.finalproject.DAO.SupplierDAO;
//import com.sa.finalproject.DAO.impl.ProductDAOImpl;
import com.sa.finalproject.DAO.impl.SupplierDAOImpl;

// tell the spring this is a controller(auto scan)
@Controller
public class ProductController {
	ApplicationContext context =  new ClassPathXmlApplicationContext("spring-module.xml");
	
	
	// To get the list of products
	@RequestMapping(value = "/productList", method = RequestMethod.GET)
	public ModelAndView getProductList(){
		// get the list of products
		ModelAndView model = new ModelAndView("ProductList");
		ProductDAO productDAO = (ProductDAO) context.getBean("productDAO");
		ArrayList<Product> productList = new ArrayList<Product>();
		productList = productDAO.getAvailableList();
		model.addObject("productList", productList);
		
		SupplierDAO  supplierDAO = (SupplierDAO)context.getBean("supplierDAO");
		ArrayList<Supplier> supplierList = new ArrayList<Supplier>();
		supplierList= supplierDAO.getList();
		model.addObject("supplierList", supplierList);
		
		return model;
	}
	
	// To add the product into the database, after inserting the product back to product list
	@RequestMapping(value = "/newProduct", method = RequestMethod.GET)
	public ModelAndView insertProduct(@ModelAttribute("productName") String name, @ModelAttribute("productPrice") String price, @ModelAttribute("productSupplier") long supCode){
		ModelAndView model = new ModelAndView("redirect:/productList");
		ProductDAO productDAO = (ProductDAO)context.getBean("productDAO");
		Product preparedProduct = new Product(name, Integer.parseInt(price), supCode);
		long newProductCode = productDAO.insert(preparedProduct);
		System.out.println("The ID of the product is : " + newProductCode + ".");
		
		return model;
	}
	
	@RequestMapping(value = "/updateProduct", method = RequestMethod.GET)
	public ModelAndView updateProduct(@ModelAttribute("id") String productID){
		// Update the product information
		ModelAndView model = new ModelAndView("ChangeProductDetail");
		ProductDAO productDAO = (ProductDAO)context.getBean("productDAO");
		Product newProductInfo = productDAO.get(Long.parseLong(productID));
//		productDAO.update(productID, newProductInfo);
		
		model.addObject("productName", newProductInfo.getProductID());
		model.addObject("productPrice", newProductInfo.getPrice());
		model.addObject("isInTheMarket", newProductInfo.isInTheMarket());
		
		SupplierDAO supplierDAO = (SupplierDAO)context.getBean("supplierDAO");
		ArrayList<Supplier> supplierList = new ArrayList<Supplier>();
		supplierList = supplierDAO.getList();
		model.addObject("supplierList", supplierList);
		return model;
	}
	
	/*
	@RequestMapping(value = "/updateProduct", method = RequestMethod.POST)
	public ModelAndView updateProduct(@ModelAttribute("producID") long productID, @ModelAttribute("productName") String newName, @ModelAttribute("productPrice") int newPrice, @ModelAttribute("isInTheMarket") boolean newProductStatus, @ModelAttribute("supplierID") long newSupplierID){
		// Update the product information
		ModelAndView model = new ModelAndView("redirect:/productList");
		ProductDAO productDAO = (ProductDAO)context.getBean("productDAO");
		Product newProductInfo = new Product(newName, newPrice, newProductStatus, newSupplierID);
		productDAO.update(productID, newProductInfo);
		
		return model;
	}*/
	
	
//	@RequestMapping(value = "/productIDSearch", method = RequestMethod.GET)
//	public ModelAndView searchID(){
//		// get the list of products
//		ModelAndView model = new ModelAndView("Testfile2");
//		ProductDAO productDAO = (ProductDAO)context.getBean("productDAO");
//		return model;
//	}
	
//	@RequestMapping(value = "/productIDSearch", method = RequestMethod.POST)
//	public ModelAndView showProduct(@ModelAttribute("producID") long productID){
//		// 
//		ModelAndView model = new ModelAndView("Testfile");
//		ProductDAO productDAO = (ProductDAO)context.getBean("productDAO");
//		Product aProduct = productDAO.get(productID);
//		
//		model.addObject("productCode", aProduct.getProductID());
//		model.addObject("productName", aProduct.getProductName());
//		model.addObject("productPrice", aProduct.getPrice());
//		model.addObject("supplierCode", aProduct.getSupplierID());
//		
//		return model;
//	}
	
	
}
