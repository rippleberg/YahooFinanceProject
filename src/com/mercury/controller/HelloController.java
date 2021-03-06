package com.mercury.controller;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;




import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.mercury.beans.Ownership;
import com.mercury.beans.OwnershipInfo;
import com.mercury.beans.Stock;
import com.mercury.beans.StockInfo;
import com.mercury.beans.User;

import com.mercury.beans.Transaction;
import com.mercury.daos.StockDao;
import com.mercury.daos.TransactionDao;
import com.mercury.daos.UserDao;

import com.mercury.services.StockService;
import com.mercury.services.TransactionService;
import com.mercury.services.UserService;

@Controller
@SessionAttributes
public class HelloController {
	@Autowired
	private TransactionDao td;
	@Autowired
	private StockDao sd;
	@Autowired
	private UserDao ud;
	@Autowired
	private TransactionService tranSS;
	@Autowired
	private StockService ss;
	@Autowired 
	private UserService us;

	public UserService getUs() {
		return us;
	}

	public void setUs(UserService us) {
		this.us = us;
	}

	public TransactionDao getTd() {
		return td;
	}

	public void setTd(TransactionDao td) {
		this.td = td;
	}

	public StockDao getSd() {
		return sd;
	}

	public void setSd(StockDao sd) {
		this.sd = sd;
	}

	@RequestMapping("/writeTest")
	public String fileWriteTest() {
		String fileName = "D:/serverfiles/myfile.csv";
		CSVReader cr;
		List<String[]> list= new ArrayList<String[]>();
		try {
			cr = new CSVReader(new FileReader(fileName));
			list = cr.readAll();
			cr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	

		try {
			CSVWriter cw = new CSVWriter(new FileWriter(fileName));
			cw.writeAll(list);
			cw.flush();
			cw.close();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return "alive";

	}

	@RequestMapping("/userTest")
	@ResponseBody
	public String testUserService() {

		// Stock stock = new Stock("NASDAQ Composite", "^IXIC");
		// sd.addStock(stock);
		Stock stock = sd.getStockByStockID(18);
		User user = ud.getUserById(16);
		String opStr = new String();

		// tranSS.addTransaction(user, stock, -500);

		StringBuffer sb = new StringBuffer();

		Set<Transaction> trans = new HashSet<Transaction>(td.queryTrans(user));

		Set<Stock> stocks = new HashSet<Stock>();
		for (Transaction t : trans) {
			stocks.add(t.getStock());
		}
		Set<StockInfo> stocksP = ss.getInfo(stocks);
		for (StockInfo s : stocksP) {
			sb.append(s.toString());
			sb.append(" <br />");
		}
		Set<Transaction> set = td.queryTransAll();
		for (Transaction t : set) {
			opStr = opStr
					+ (t.getAmount() + ";" + t.getTransid() + ";"
							+ t.getTimestamp() + ";" + t.getUnitprice())
					+ "<br/>";
			opStr = opStr + t.getStock().toString() + "<br/>";
			opStr = opStr + t.getUser().getUserName() + ":"
					+ t.getUser().getPassword() + "<br/>";

			opStr = opStr + "<br/><br/>";
		}

		return sb.toString() + "<br/>" + opStr;
	}

	@RequestMapping("/test")
	public String test() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("alice");
		return "alive";
	}
	
	@RequestMapping(value="/login", method = RequestMethod.GET)
	public String login(ModelMap model) {
		return "login";
	}

	@RequestMapping("/BackendTest")
	@ResponseBody
	public String BackendTest() {	
		
		/*Stock stock = new Stock("yahoo", "yo");
		sd.addStock(stock);
		

		User user = new User(10000,"qwer","123","abc@gmail.com",new BigDecimal(10),"admin",0);
		ud.save(user);
		Transaction tran = new Transaction(user,stock);
		td.addTrans(tran);*/

		User user = ud.getUserById(16);
		String opStr = new String();

		/* tranSS.addTransaction(user, stock, 30); */

		/*
		 * User user = new User(10000,"qwer","123","abc@gmail.com",new
		 * BigDecimal(10),"admin",0); ud.save(user); Transaction tran = new
		 * Transaction(user,stock); td.addTrans(tran);
		 */

		// User user = new User(10000,"qwer","123","abc@gmail.com",new
		// BigDecimal(10),"admin",0);

		/*
		 * User user1 = ud.getUserById(16); opStr = user1.getUserId() +
		 * user1.getUserName() + user1.getPassword() +"<br/>";
		 */

		Set<Transaction> set = td.queryTransAll();
		for (Transaction t : set) {
			opStr = opStr
					+ (t.getAmount() + ";" + t.getTransid() + ";"
							+ t.getTimestamp() + ";" + t.getUnitprice())
					+ "<br/>";
			opStr = opStr + t.getStock().toString() + "<br/>";
			opStr = opStr + t.getUser().getUserName() + ":"
					+ t.getUser().getPassword() + "<br/>" + ":"
					+ t.getUser().getUserId();

			opStr = opStr + "<br/><br/>";
		}

		return opStr;
	}


	@RequestMapping(value="/main", method = RequestMethod.GET)
	public ModelAndView mainPage() {
		String userName = SecurityContextHolder.getContext().getAuthentication().getName();
		ModelAndView mav = new ModelAndView();
		OwnershipInfo owninfo = us.getOwnershipInfoByUserName(userName);
		Set<StockInfo> infos = new HashSet<StockInfo>();
		for (Ownership own : owninfo.getOwnerships()) {
			StockInfo si = ss.getInfo(own.getStock());
			si.setAmount(own.getQuantity());
			infos.add(si);
		}
		
		mav.setViewName("main");
		mav.addObject("ownershipInfo", owninfo);
		mav.addObject("balance", us.getBalance(userName));
		mav.addObject("stockInfos", infos);
		return mav;
	}
	
	@RequestMapping(value="/admin", method = RequestMethod.GET)
	public ModelAndView adminPage() {
		String adminName = SecurityContextHolder.getContext().getAuthentication().getName();
		ModelAndView mav = new ModelAndView();
		mav.setViewName("admin");
		mav.addObject("title", "Welcome back admin " + adminName);
		return mav;
	}
	
	
	@RequestMapping(value="/history", method = RequestMethod.GET)
	public ModelAndView historyPage() {
		System.out.println("history.........................");
		ModelAndView mav = new ModelAndView();
		mav.setViewName("history");
		return mav;
	}
	@RequestMapping(value="/activateAccount", method = RequestMethod.GET)
	public ModelAndView activeMail(HttpServletRequest request) {
		String username = request.getParameter("username");
		User user = ud.getUserByUsername(username);
		user.setEnable(1);
		ud.updateUser(user);
		ModelAndView mav = new ModelAndView();
		mav.setViewName("active_confirm");
		return mav;
	}

//	@RequestMapping(value="/main", method = RequestMethod.GET)
//	@ResponseBody
//	public String mainPage() {
//		StringBuffer sb = new StringBuffer();
//		for(Stock stock:us.ownsStocksByUserId(8)) {
//			//System.out.println(stock.toString());
//			sb.append(stock.getSid() + " " + stock.getScode());
//			sb.append("\n");
//		}
//		//return us.ownsStocksByUserId(8).toString();
//		return sb.toString();
//	}
}
