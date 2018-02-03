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
	 * ��¼action
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
					HttpSession session = request.getSession(true);// û�е�,���᷵�ؿ�,�ᴴ��һ��session;
					session.setAttribute("st", stu);// ��stu����ŵ��Ự�ռ���;
					session.setAttribute("qflg", "0");//
					target = "t";
					// System.out.println("SNO " + stu.getSno());
					if (stu.getSno().equals("admin"))
						target = "admin";
				} else {
					request.setAttribute("stinfo", "�������");
				}
			} else {
				request.setAttribute("stinfo", "�û�������");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mapping.findForward(target);
	}

	/*
	 * ע���action
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
		if (!(student.getSno().trim().equals("")) && student.getSno() != null)// tirm()��ȥ����β�Ŀո�;
		{
			try {
				if (sts.getStudentbySno(student.getSno()) == null) {
					b = sts.save(student);
					if (b) {// �ж����b����ɹ��Ļ�,��ִ����������;
						request.setAttribute("regist", "ע��ɹ�");
						if ("admin".equals(flag))
							target = "admin";
					} else {
						request.setAttribute("regist", "ע��ʧ��");
						if ("admin".equals(flag))
							target = "admin";
					}
				} else {
					request.setAttribute("regist", "�û��Ѵ���");
					if ("admin".equals(flag))
						target = "admin";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return mapping.findForward(target);// ��ת��struts-config.xml�е�action
	}

	/*
	 * ��ѧ������
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
	 * ɾ��ѧ��;
	 */
	public ActionForward delStudent(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		int id = Integer.parseInt(request.getParameter("sid"));
		try {
			Student s = sts.getStudentbyId(id);// ����StudentService�����е�getgetStudentbyId����;
			if (s != null) {
				sts.del(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ɾ�����ɹ�,������");
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
