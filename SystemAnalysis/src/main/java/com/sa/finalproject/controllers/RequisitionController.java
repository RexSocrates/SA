package com.sa.finalproject.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.sa.finalproject.DAO.BillOfPurchaseDAO;
import com.sa.finalproject.DAO.ProductDAO;
import com.sa.finalproject.DAO.PurchasingRequisitionDAO;
import com.sa.finalproject.DAO.SupplierDAO;
import com.sa.finalproject.DAO.impl.EmployeeDAOImpl;
import com.sa.finalproject.entity.Employee;
import com.sa.finalproject.entity.Product;
import com.sa.finalproject.entity.PurchaseOrder;
import com.sa.finalproject.entity.PurchasingRequisition;
import com.sa.finalproject.entity.Supplier;
import com.sa.finalproject.entity.displayClass.DisplayPR;
import com.sa.finalproject.entity.supportingClass.PurchasedProduct;

@Controller
@SessionAttributes(value = {"newaccount", "shoppingCart"})
public class RequisitionController {
	
	@Autowired
	PurchaseOrder cart_session;
	
	ApplicationContext context = new ClassPathXmlApplicationContext("spring-module.xml");
	
	// Indicate whether the user has selected the supplier form
	static boolean hasSelectedSupplier = false;
	Long selectedSupplier = null;
	
	@RequestMapping(value = "/insertRequisition", method = RequestMethod.GET)
	public ModelAndView checkRequisition(){
		// 顯示開立請購單頁面
		ModelAndView model = new ModelAndView("insertRequisition");
		SupplierDAO supplierDAO = (SupplierDAO)context.getBean("supplierDAO");
		ArrayList<Supplier> supplierList = new ArrayList<Supplier>();
		supplierList = supplierDAO.getList();
		model.addObject("supplierList", supplierList);
		
		
		if(this.hasSelectedSupplier && this.selectedSupplier != null) {
			ProductDAO productDAO = (ProductDAO)context.getBean("productDAO");
			ArrayList<Product> productList = new ArrayList<Product>();
			productList = productDAO.getAvailableProductOf(this.selectedSupplier);
			model.addObject("productList", productList);
			model.addObject("selectedSupplier", this.selectedSupplier);
			this.selectedSupplier = null;
			this.hasSelectedSupplier = false;
			
			model.addObject("submit", "submit");
		}else {
			model.addObject("productList", new ArrayList<Product>());
			model.addObject("submit", null);
		}
		
		return model;
	}
	
	@RequestMapping(value = "/insertRequisition", method = RequestMethod.POST)
	public ModelAndView insertRequisition(@ModelAttribute("selectedSupplierID")String selectedID){
		// show all the products of that selected supplier
		ModelAndView model = new ModelAndView("redirect:/insertRequisition");
		this.selectedSupplier = Long.parseLong(selectedID);
		this.hasSelectedSupplier = true;
		
		return model;
	}
	
	@RequestMapping(value = "/addProductToRequisition", method = RequestMethod.POST)
	public ModelAndView addProductToShoppingCart(@ModelAttribute("id")String productID, @ModelAttribute("amount")String amount){
		// add the product to the shopping cart
		ModelAndView model = new ModelAndView("redirect:/previewDetailRequisition");
		ProductDAO productDAO = (ProductDAO)context.getBean("productDAO");
		Product product = productDAO.get(Long.parseLong(productID));
		
		// get supplier information
		SupplierDAO supplierDAO = (SupplierDAO)context.getBean("supplierDAO");
		Supplier supplier = new Supplier();
		supplier = supplierDAO.get(product.getSupplierID());
		
		PurchasedProduct purchasedItem = new PurchasedProduct();
		purchasedItem.setProduct(product, Integer.parseInt(amount));
		purchasedItem.setSupplierID(supplier.getSupplierID());
		purchasedItem.setSupplierName(supplier.getSupplierName());
		
		boolean productInTheCart = false;
		for(int i = 0; i < cart_session.getList().size(); i++) {
			PurchasedProduct currentProduct = cart_session.getList().get(i);
			System.out.println("Product name" + currentProduct.getProductName());
			if(String.valueOf(currentProduct.getProductID()).equals(productID)) {
//				currentProduct.addOne();
				int originalAmount = currentProduct.getPurchasingAmount();
				currentProduct.setPurchasingAmount(originalAmount + Integer.parseInt(amount));
				productInTheCart = true;
				break;
			}
		}
		
		if(!productInTheCart) {
			System.out.println("Add the product whose ID is " + purchasedItem.getProductID());
			cart_session.add(purchasedItem);
		}
		
		return model;
	}
	
	
	// 從購物車中刪掉商品
	@RequestMapping(value = "/dropProductFromCart", method = RequestMethod.GET)
	public ModelAndView dropProduct(@ModelAttribute("id")String id){
	
		ModelAndView model = new ModelAndView("redirect:/previewDetailRequisition");
		
		for(int i = 0; i < cart_session.getList().size(); i++) {
			PurchasedProduct currentProduct = cart_session.getList().get(i);
			if(String.valueOf(currentProduct.getProductID()).equals(id)) {
				// drop the product
				System.out.println("The product has been deleted : " + cart_session.drop(i));
			}
		}
		
		
		return model;
	}
	
	
	
	@RequestMapping(value = "/listRequisition", method = RequestMethod.GET)
	public ModelAndView listRequisition(){
	
		ModelAndView model = new ModelAndView("Requisition");
		PurchasingRequisitionDAO requisitionDAO = (PurchasingRequisitionDAO)context.getBean("purchaseRequisitionDAO");
		ArrayList<PurchasingRequisition> requisitionList = new ArrayList<PurchasingRequisition>();
		requisitionList = requisitionDAO.getList();
		
		
		ArrayList<DisplayPR> displayList = new ArrayList<DisplayPR>();
		for(int i = 0; i < requisitionList.size(); i++) {
			PurchasingRequisition currentRequisition = requisitionList.get(i);
			DisplayPR displayPR = new DisplayPR();
			displayPR.setRequisition(currentRequisition);
			
			Supplier supplier = new Supplier();
			supplier = requisitionDAO.getASupplierOf(currentRequisition.getPrSerial());
			displayPR.setSupplier(supplier);
			
			displayList.add(displayPR);
		}
		
		System.out.println("The length of requisition list : " + requisitionList.size());
		System.out.println("The length of display list : " + displayList.size());
		
		model.addObject("prList", displayList);
		
		return model;
	}
	
	
	@RequestMapping(value = "/listDetailRequisition", method = RequestMethod.GET)
	public ModelAndView listDetailRequisition(@ModelAttribute("id")String prSerial){
		// 列出單一請購單的詳細資料
		ModelAndView model = new ModelAndView("RequisitionDetail");
		
		if(prSerial.length() == 0) {
			prSerial = "0";
		}
		
		
		PurchasingRequisitionDAO requisitionDAO = (PurchasingRequisitionDAO)context.getBean("purchaseRequisitionDAO");
		EmployeeDAOImpl staffDAO = (EmployeeDAOImpl)context.getBean("EmployeeDAO");
		PurchasingRequisition pr = requisitionDAO.get(Long.parseLong(prSerial)); 
		PurchaseOrder content = new PurchaseOrder();
		content = pr.getRequisitionContent();
		
		ProductDAO productDAO = (ProductDAO)context.getBean("productDAO");
		for(int i = 0; i < content.count(); i++) {
			PurchasedProduct currentItem = content.getList().get(i);
			String productName = productDAO.get(currentItem.getProductID()).getProductName();
			currentItem.setProductName(productName);
		}
		
		Employee staff = new Employee();
		staff = staffDAO.getAEmployee(pr.getEmployeeID());
		
		Supplier supplier = new Supplier();
		supplier = requisitionDAO.getASupplierOf(Long.parseLong(prSerial));
		model.addObject("prSupplier", supplier.getSupplierName());
		model.addObject("prSupplierGrade", supplier.getLevel());
		model.addObject("prEmployeeId", staff.getName());
		model.addObject("prDate", pr.getDate());
		model.addObject("prContent", content.getList());
		model.addObject("prListAmount", content.getListPrice());
		model.addObject("prSerial", prSerial);
		
		return model;
	}
	
	@RequestMapping(value = "/previewDetailRequisition", method = RequestMethod.GET)
	public ModelAndView showCart() {
		ModelAndView model = new ModelAndView("previewDetailRequisition");
		// 顯示購物車內容物
		
		model.addObject("cartList", cart_session.getList());
		model.addObject("listPrice", cart_session.getListPrice());
		return model;
	}
	
	
	@RequestMapping(value = "/previewDetailRequisition", method = RequestMethod.POST)
	public ModelAndView insertCart(HttpSession session) {
		ModelAndView model = new ModelAndView("redirect:/listRequisition");
		// 送出購物車內容
		String accountID = "0";
		if(session.getAttribute("newaccount") != null) {
			Employee staff = (Employee)session.getAttribute("newaccount");
			accountID = staff.getId();
			System.out.println("Staff ID : " + staff.getId() + ".");
		}else {
			System.out.println("Session is null");
		}
		PurchasingRequisitionDAO requisitionDAO = (PurchasingRequisitionDAO)context.getBean("purchaseRequisitionDAO");
		
		ArrayList<Long> aLevelList = new ArrayList<Long>();
		aLevelList = requisitionDAO.insert(cart_session, Long.parseLong(accountID));
		
		BillOfPurchaseDAO bopDAO = (BillOfPurchaseDAO)context.getBean("BillOfPurchaseDAO");
		for(int i = 0; i < aLevelList.size(); i++) {
			PurchasingRequisition currentPR = new PurchasingRequisition();
			long currentSerial = aLevelList.get(i);
			currentPR = requisitionDAO.get(currentSerial);
			bopDAO.transferIntoBOP(currentPR);
		}
		
		cart_session.cleanTheList();
		
		return model;
	}
	
	
	@RequestMapping(value = "/confirmRequisition", method = RequestMethod.GET)
	public ModelAndView confirmRequisition(@ModelAttribute("id")String serial, HttpSession session){
		long managerID = 0;
		if(session.getAttribute("newaccount") != null) {
			Employee staff = (Employee)session.getAttribute("newaccount");
			managerID = Long.parseLong(staff.getId());
		}
		
		// 確認請購單
		if(serial.length() == 0) {
			serial = "0";
		}
		ModelAndView model = new ModelAndView("redirect:/Order");
		BillOfPurchaseDAO bopDAO = (BillOfPurchaseDAO)context.getBean("BillOfPurchaseDAO");
		PurchasingRequisitionDAO requisitionDAO = (PurchasingRequisitionDAO)context.getBean("purchaseRequisitionDAO");
		PurchasingRequisition pr = requisitionDAO.get(Long.parseLong(serial));
		
		bopDAO.transferIntoBOP(pr);
		
		requisitionDAO.confirm(Long.parseLong(serial), managerID);
		
		return model;
	}
	
	@RequestMapping(value = "/cancelRequisition", method = RequestMethod.GET)
	public ModelAndView cancelRequisition(HttpSession session, @ModelAttribute("id")String serial) {
		// 退回請購單
		ModelAndView model = new ModelAndView("redirect:/listRequisition");
		
		long managerID = 0;
		if(session.getAttribute("newaccount") != null) {
			Employee staff = new Employee();
			staff = (Employee)session.getAttribute("newaccount");
			managerID = Long.parseLong(staff.getId());
		}
		
		if(serial.length() == 0) {
			serial = "0";
		}
		
		PurchasingRequisitionDAO requisitionDAO = (PurchasingRequisitionDAO)context.getBean("purchaseRequisitionDAO");
		requisitionDAO.confirm(managerID, Long.parseLong(serial), false);
		
		return model;
	}
	
	
	@RequestMapping(value = "/insertFakeRequisition", method = RequestMethod.GET)
	public ModelAndView insertFakeRequisition(){
		// 開立請購單動作執行與測試
		ModelAndView model = new ModelAndView("redirect:/listRequisition");
		PurchasingRequisitionDAO requisitionDAO = (PurchasingRequisitionDAO)context.getBean("purchaseRequisitionDAO");
		PurchaseOrder mixedOrder = this.getFakeOrder();
		System.out.println("The length of mixed order is " + mixedOrder.getList().size());
		
		ArrayList<Long> aLevelList = new ArrayList<Long>();
		aLevelList = requisitionDAO.insert(mixedOrder, Long.parseLong("403401213"));
		
		BillOfPurchaseDAO bopDAO = (BillOfPurchaseDAO)context.getBean("BillOfPurchaseDAO");
		for(int i = 0; i < aLevelList.size(); i++) {
			PurchasingRequisition currentPR = new PurchasingRequisition();
			long currentSerial = aLevelList.get(i);
			currentPR = requisitionDAO.get(currentSerial);
			bopDAO.transferIntoBOP(currentPR);
		}
		
		
//		System.out.println("The supplier level is " + supplierCurrentLevel);
		
		return model;
	}
	
	public PurchaseOrder getFakeOrder() {
		PurchaseOrder fakeOrder = new PurchaseOrder();
		
		ProductDAO productDAO = (ProductDAO) context.getBean("productDAO");
		ArrayList<Product> productList = new ArrayList<Product>();
		productList = productDAO.getList();
		
		SupplierDAO supplierDAO = (SupplierDAO)context.getBean("supplierDAO");
		
		for(int i = 0; i < productList.size(); i++) {
			Product currentProduct = productList.get(i);
			int randomAmount = (int)(Math.random() * 20 + 1);
			Supplier currentSupplier = supplierDAO.get(currentProduct.getSupplierID());
			PurchasedProduct purchasedProduct = new PurchasedProduct(currentProduct.getProductID(), currentProduct.getProductName(), randomAmount, currentSupplier.getSupplierID(), currentSupplier.getSupplierName());
			fakeOrder.add(purchasedProduct);
		}
		
		return fakeOrder;
	}
}