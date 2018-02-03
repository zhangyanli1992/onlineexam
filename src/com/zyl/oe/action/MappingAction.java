 package com.zyl.oe.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.*;
import org.apache.struts.actions.*;

import com.zyl.oe.entity.Student;

import com.zyl.oe.Serv.ExamService;
import com.zyl.oe.Serv.StudentService;
import com.zyl.oe.ServImpl.ExamServiceImpl;
import com.zyl.oe.ServImpl.StudentServiceImp;

public class MappingAction extends MappingDispatchAction {

	/*
	 * 登录action
	 */
	StudentService sts = new StudentServiceImp();

	ExamService es = new ExamServiceImpl();

	public ActionForward login(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String target = "f";
		String sno = request.getParameter("sno");
		String passwd = request.getParameter("password");

		try {
			Student stu = sts.getStudentbySno(sno);
			if (stu != null) {
				if (stu.getPassword().equals(passwd)) {
					HttpSession session = request.getSession(true);// 没有的,不会返回空,会创建一个session;
					session.setAttribute("st", stu);// 将stu对象放到会话空间中;
					session.setAttribute("qflg", "0");//
					target = "t";
					// System.out.println("SNO " + stu.getSno());
					if (stu.getSno().equals("admin"))
						target = "admin";
				} else {
					request.setAttribute("stinfo", "密码错误");
				}
			} else {
				request.setAttribute("stinfo", "用户不存在");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mapping.findForward(target);
	}

	/*
	 * 注册的action
	 */
	public ActionForward register(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String target = "t";
		boolean b;
		Student student = new Student();
		student.setSno(request.getParameter("sno"));
		student.setPassword(request.getParameter("password"));
		student.setName(new String(request.getParameter("sname").getBytes(
				"ISO-8859-1"), "GBK"));
		student.setHumanId(request.getParameter("HumanId"));
		student.setClassName(new String(request.getParameter("className")
				.getBytes("ISO-8859-1"), "GBK"));
		student.setEmail(request.getParameter("email"));
		student.setPhone(request.getParameter("phone"));
		student.setAddress(request.getParameter("address"));
		//System.out.println(student.getName());
		String flag = request.getParameter("admin");
		if (!(student.getSno().trim().equals("")) && student.getSno() != null)// tirm()是去除首尾的空格;
		{
			try {
				if (sts.getStudentbySno(student.getSno()) == null) {
					b = sts.save(student);
					if (b) {// 判断如果b保存成功的话,就执行下面的语句;
						request.setAttribute("regist", "注册成功");
						if ("admin".equals(flag))
							target = "admin";
					} else {
						request.setAttribute("regist", "注册失败");
						if ("admin".equals(flag))
							target = "admin";
					}
				} else {
					request.setAttribute("regist", "用户已存在");
					if ("admin".equals(flag))
						target = "admin";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return mapping.findForward(target);// 跳转到struts-config.xml中的action
	}

	/*
	 * 查学生所有
	 * 
	 */
	public ActionForward listAll(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
			List<Student> stds = sts.getStudentAll();
			request.setAttribute("stds", stds);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapping.findForward("list");
	}

	/*
	 * 删除学生;
	 */
	public ActionForward delStudent(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		int id = Integer.parseInt(request.getParameter("sid"));
		try {
			Student s = sts.getStudentbyId(id);// 调用StudentService方法中的getgetStudentbyId方法;
			if (s != null) {
				sts.del(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("删除不成功,请重试");
		}
		return mapping.findForward("del");
	}
	
	public ActionForward returnIndex(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		HttpSession session = request.getSession(false);
		session.removeAttribute("st");
		session.invalidate();
		return mapping.findForward("index");
	}

}
