package com.sa.finalproject.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.sa.finalproject.DAO.BillOfPurchaseDAO;
import com.sa.finalproject.DAO.PurchasingRequisitionDAO;
import com.sa.finalproject.DAO.impl.EmployeeDAOImpl;
import com.sa.finalproject.DAO.impl.SupplierDAOImpl;
import com.sa.finalproject.entity.BillOfPurchase;
import com.sa.finalproject.entity.Employee;
import com.sa.finalproject.entity.PurchaseOrder;
import com.sa.finalproject.entity.PurchasingRequisition;
import com.sa.finalproject.entity.Supplier;
import com.sa.finalproject.entity.displayClass.DisplayBOP;

@Controller
@SessionAttributes(value = {"newaccount", "shoppingCart"})
public class BillOfPurchaseController {
	ApplicationContext context =  new ClassPathXmlApplicationContext("spring-module.xml");
	
	
	@RequestMapping(value = "/Order", method = RequestMethod.GET)
	public ModelAndView getBOPList(){
		//顯示所有進貨單
		ModelAndView model = new ModelAndView("BillOfPurchase");
		BillOfPurchaseDAO billOfPurchaseDAO = (BillOfPurchaseDAO)context.getBean("BillOfPurchaseDAO");
	    ArrayList<BillOfPurchase> billOfPurchaseList = new ArrayList<BillOfPurchase>();
		billOfPurchaseList = billOfPurchaseDAO.getList();
		
		PurchasingRequisitionDAO prDAO = (PurchasingRequisitionDAO)context.getBean("purchaseRequisitionDAO");
		SupplierDAOImpl supplierDAO = (SupplierDAOImpl)context.getBean("supplierDAO");
		ArrayList<DisplayBOP> displayBOPList = new ArrayList<DisplayBOP>();
		for(int i = 0; i < billOfPurchaseList.size(); i++) {
			DisplayBOP displayBOP = new DisplayBOP();
			BillOfPurchase currentBOP = billOfPurchaseList.get(i);
			Supplier supplier = new Supplier();
			
			supplier = prDAO.getASupplierOf(currentBOP.getBopSerial());
			
			displayBOP.setBOP(currentBOP);
			displayBOP.setSupplier(supplier);
			displayBOP.setSupplierName(supplier.getSupplierName());
			System.out.println("Supplier name : " + supplier.getSupplierName());
			
			
			
			displayBOPList.add(displayBOP);
		}
		
		
		model.addObject("bopList", displayBOPList);
		
		return model;
	}
	@RequestMapping(value = "/bopDetailList", method = RequestMethod.GET)
	public ModelAndView detailList(@ModelAttribute("id") String bopserial){
		//列出單一進貨單明細
		ModelAndView model = new ModelAndView("BillOfPurchaseDetail");
		BillOfPurchaseDAO billOfPurchaseDAO = (BillOfPurchaseDAO)context.getBean("BillOfPurchaseDAO");
		PurchasingRequisitionDAO prDAO = (PurchasingRequisitionDAO)context.getBean("purchaseRequisitionDAO");
		EmployeeDAOImpl staffDAO = (EmployeeDAOImpl)context.getBean("EmployeeDAO");
		
		if(bopserial.length() == 0) {
			bopserial = "0";
		}
		
		PurchaseOrder content = new PurchaseOrder();
		content = billOfPurchaseDAO.getContentOf(Long.parseLong(bopserial));
		BillOfPurchase bop = billOfPurchaseDAO.get(Long.parseLong(bopserial));
		
		Supplier supplier = new Supplier();
		supplier = prDAO.getASupplierOf(Long.parseLong(bopserial));
		
		Employee staff = new Employee();
		staff = staffDAO.getAEmployee(bop.getEmployeeID());
		
		if(bop.getRemarks() != null) {
			model.addObject("bopRemark", bop.getRemarks());
		}else {
			model.addObject("bopRemark", "");
		}
		model.addObject("bopSerial", bop.getBopSerial());
		model.addObject("productList", content.getList());
		model.addObject("isHasPaid", bop.isHasPaid());
		model.addObject("bopSupplier", supplier.getSupplierName());
		model.addObject("bopTotalAmount", content.getListPrice());
		model.addObject("bopEmployee", staff.getName());
		model.addObject("bopTime", bop.getDateTime());
		
		
		
		
		
		return model;
	}
	
	@RequestMapping(value = "/unpaidList", method = RequestMethod.GET)
	public ModelAndView showUnpaidProductList(){
		//顯示到貨且未付款訂單
		ModelAndView model = new ModelAndView("ShowUpaidList");
		BillOfPurchaseDAO billOfPurchaseDAO = (BillOfPurchaseDAO)context.getBean("BillOfPurchaseDAO");
		List<BillOfPurchase> billOfPurchaseList = new ArrayList<BillOfPurchase>();
		billOfPurchaseList = billOfPurchaseDAO.showUnpaidProduct();
		model.addObject("showUnpaidProductlist", billOfPurchaseList);
		return model;
	}

//	
//	@RequestMapping(value = "/billofpurchase", method = RequestMethod.POST) // 將請購單轉為進貨單
//	public ModelAndView transferIntoBOP(PurchasingRequisition aPurchaseingRequisition){
//		ModelAndView model = new ModelAndView("billofpurchase");
//		BillOfPurchaseDAO transferIntoBOP = (BillOfPurchaseDAO)context.getBean("BillOfPurchaseDAO");
//		transferIntoBOP.transferIntoBOP(aPurchaseingRequisition);
//		return model;
//	}
    @RequestMapping(value = "/billofpurchaseupdate", method = RequestMethod.GET) //更新資料
	public ModelAndView updateBillOfPurchasePage(@ModelAttribute("id") String serial){
		ModelAndView model = new ModelAndView("Testfile2");
		BillOfPurchaseDAO update = (BillOfPurchaseDAO)context.getBean("BillOfPurchaseDAO");
		BillOfPurchase bop = update.get(Long.parseLong(serial));
		model.addObject("update",bop);
		return model;
	}
	
	@RequestMapping(value = "/billofpurchaseupdate", method = RequestMethod.POST)
	public ModelAndView updateBillOfPurchase(@ModelAttribute("id") String bop, BillOfPurchase aBillOfPurchase, @ModelAttribute("arrived") Boolean arrived,@ModelAttribute("remark")String remark
			,@ModelAttribute("passed") Boolean passed,@ModelAttribute("paid") Boolean paid){
		ModelAndView model = new ModelAndView("redirect:/bopDetailList");
		BillOfPurchaseDAO update = (BillOfPurchaseDAO)context.getBean("BillOfPurchaseDAO");
		aBillOfPurchase.setRemarks(remark);
		aBillOfPurchase.setHasPaid(paid);
		aBillOfPurchase.setPassed(passed);
		aBillOfPurchase.setHasArrived(arrived);
		update.update(Long.parseLong(bop),aBillOfPurchase);	
		return model;
	}
	
	@RequestMapping(value = "/transferBill", method = RequestMethod.GET)
	public ModelAndView transferingBill(){
		ModelAndView model = new ModelAndView("redirect:/Order");
//		long[] testArr = new long[5];
//		for(int i = 0; i <= testArr.length; i++) {
//			testArr[i] = i + 25;
//		}
		BillOfPurchaseDAO bopDAO = (BillOfPurchaseDAO)context.getBean("BillOfPurchaseDAO");
		PurchasingRequisitionDAO requisitionDAO = (PurchasingRequisitionDAO)context.getBean("purchaseRequisitionDAO");
		PurchasingRequisition requisition = requisitionDAO.get(27);
		bopDAO.transferIntoBOP(requisition);
		
		
		return model;
	}
}
//@RequestMapping(value = "/bopDetailList", method = RequestMethod.POST) // 驗貨
//public ModelAndView examineGoods(@ModelAttribute("id") long aBopSerial,boolean passed){
//	ModelAndView model = new ModelAndView("redirect:/bopList");
//	BillOfPurchaseDAO examieGoods = (BillOfPurchaseDAO)context.getBean("BillOfPurchaseDAO");
//	examieGoods.examineGoods(aBopSerial,passed);
//
//	return model;
//}

//@RequestMapping(value = "/billofpurchase", method = RequestMethod.POST) // 付款
//public ModelAndView paid(long aBopSerial){
//	ModelAndView model = new ModelAndView("billofpurchase");
//	BillOfPurchaseDAO paid = (BillOfPurchaseDAO)context.getBean("BillOfPurchaseDAO");
//	paid.paid(aBopSerial);
//	return model;
//}
/*
	@RequestMapping(value = "/remark", method = RequestMethod.GET) //顯示所有備註
	public ModelAndView getRemarkList(@ModelAttribute("bopSerial") long bopSerial){
		ModelAndView model = new ModelAndView("remark");
		RemarkDAO remark = (RemarkDAO)context.getBean("RemarkDAO");
		ArrayList<Remark> remarkList = new ArrayList<Remark>();
		remarkList = remark.showRemark(bopSerial);
		model.addObject("remarkList", remarkList);
		
		return model;
	}
	
	@RequestMapping(value = "/insertRemark", method = RequestMethod.GET)
	public ModelAndView insertProductPage(){
		ModelAndView model = new ModelAndView("insertRemark");
		return model;
	}
	
	@RequestMapping(value = "/insertRemark", method = RequestMethod.POST)
	public ModelAndView insertProduct(@ModelAttribute long bop_serial ,Remark remark){
		ModelAndView model = new ModelAndView("redirect:/billofpurchase");
		RemarkDAO remarkInsert = (RemarkDAO)context.getBean("RemarkDAO");
		remarkInsert.addRemark(bop_serial,remark);
		return model;
	}
	@RequestMapping(value = "/updateRemark", method = RequestMethod.GET)
	public ModelAndView updateProductPage(@ModelAttribute long bop_serial ,Remark remark){
		ModelAndView model = new ModelAndView("updateProduct");
		RemarkDAO remarkUpdate = (RemarkDAO) context.getBean("RemarkDAO");
		// ArrayList<BillOfPurchaseDAO> remarks  = new  ArrayList<BillOfPurchaseDAO>();
		remarkUpdate =  (RemarkDAO)remarkUpdate.showRemark(bop_serial);
		model.addObject("remark", remarkUpdate);
		return model;
	}
	
	@RequestMapping(value = "/updateRemark", method = RequestMethod.POST)
	public ModelAndView updateProduct(@ModelAttribute long bop_serial, Remark remark){
		ModelAndView model = new ModelAndView("redirect:/billofpurchase");
		RemarkDAO remarkUpdate = (RemarkDAO) context.getBean("RemarkDAO");
		remarkUpdate.updateRemark(bop_serial,remark);	
		return model;
	}

	@RequestMapping(value = "/123", method = RequestMethod.POST)
	public ModelAndView deleteProduct(@ModelAttribute long bop_serial, Remark remark){
		ModelAndView model = new ModelAndView("redirect:/billofpurchase");
		RemarkDAO remarkDelete = (RemarkDAO) context.getBean("RemarkDAO");
		remarkDelete.deleteRemark(bop_serial,remark);
		return model;
	}
   

}
*/