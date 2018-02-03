package com.zyl.oe.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.*;
import org.apache.struts.actions.*;

import com.zyl.oe.Serv.ExamService;
import com.zyl.oe.Serv.QuestionServiceIf;
import com.zyl.oe.Serv.StudentService;
import com.zyl.oe.ServImpl.ExamServiceImpl;
import com.zyl.oe.ServImpl.QuestionServiceImp;
import com.zyl.oe.ServImpl.StudentServiceImp;
import com.zyl.oe.entity.ExamPaper;
import com.zyl.oe.entity.Examreport;
import com.zyl.oe.entity.Question;
import com.zyl.oe.entity.Student;
import com.zyl.oe.form.ExampaperForm;

/*
 * ���Ե�action�������ƿ������Ե�ҳ����ת,
 * 
 */
public class ExamServices extends MappingDispatchAction {
	// �������Ե�ҵ��ʵ��,,����ҵ��ʵ��,ѧ����ʱ��,����ŵ�ȫ�ֵı���;
	ExamService es = new ExamServiceImpl();

	QuestionServiceIf qsi = new QuestionServiceImp();

	StudentService sts = new StudentServiceImp();

	// ��ҳ��������ɵ�ҵ�Ծ�action;
	public ActionForward exampaperList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		HttpSession hs = request.getSession(false);// ;�ж��Ƿ����,��������ڷ��ؿ�;
		String termnum = request.getParameter("term");// ��ҳ���϶�ȡѧ�ڲ���;

		try {
			if (hs.getAttribute("qflg").equals("0"))// �ж�
			{
				List<Question> eps = new ArrayList<Question>();
				eps = es.generatorPaper(20); // ����һ�����Ϊ��ʮ��Ŀ���Ծ�
				hs.setAttribute("eps", eps); // �����ɵ��Ծ�ŵ��Ự��;
				hs.setAttribute("qflg", "1");
				hs.setAttribute("term", termnum);
			}
		} catch (Exception e) {

			e.printStackTrace();
		}

		return mapping.findForward("list");// ҳ�����ת;
	}

	// �ɼ����ж�
	public ActionForward graded(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String[] answers = ((ExampaperForm) form).getAnswers();// ͨ�������ĵ�ҳ��������Ĵ�;
		Student s;
		HttpSession hs = request.getSession(false);
		List<ExamPaper> eps = new ArrayList<ExamPaper>();// �Ծ���б�
		List<Question> qs = (List<Question>) hs.getAttribute("eps"); // �ӻỰ�еõ����е�����;
		s = (Student) hs.getAttribute("st");// �õ��Ự�е�ѧ������;
		int i = 1;
		for (Question q : qs)// ������Ŀ
		{
			// �����Ự�е�������ӵ��Ծ���;
			ExamPaper ep = new ExamPaper();
			ep.setQuestion(q);
			ep.setAnswer(answers[i]);
			// System.out.println(answers[i]);
			i++;
			eps.add(ep);
		}
		// ������Ӧ��ҵ�񷽷�����������;
		int erid = es.submit(s, eps);
		int score = es.autograde(erid);
		score *= 5;
		try {
			Examreport er = es.getExamreport(erid);// �õ����Ծ�ķ���;
			er.setTerm((String) hs.getAttribute("term"));// �õ�ҳ���е�term����;
			er.setScore(score);
			es.modify(er, score);
			request.setAttribute("score", score);// ���ɼ��ŵ���ǰ������ҳ����;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapping.findForward("t");// ҳ�����ת;
	}

	// �õ�����������б�;
	public ActionForward qlist(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
			List<Question> qs = qsi.getQuestionALl();
			request.setAttribute("qs", qs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapping.findForward("list");
	}

	// ɾ����Ŀ;
	public ActionForward delQuestion(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		int id = Integer.parseInt(request.getParameter("qid"));
		try {
			Question q = es.selectQuestion(id);
			if (q != null) {
				qsi.del(q);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapping.findForward("delete");
	}

	// �����Ŀ;
	public ActionForward addQuestion(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		boolean b;
		Question q = new Question();
		q.setContext(new String(request.getParameter("context").getBytes(
				"ISO-8859-1"), "GBK")); // �����������ת��
		q.setAnswer1(new String(request.getParameter("answer1").getBytes(
				"ISO-8859-1"), "GBK"));
		q.setAnswer2(new String(request.getParameter("answer2").getBytes(
				"ISO-8859-1"), "GBK"));
		q.setAnswer3(new String(request.getParameter("answer3").getBytes(
				"ISO-8859-1"), "GBK"));
		q.setAnswer4(new String(request.getParameter("answer4").getBytes(
				"ISO-8859-1"), "GBK"));
		q.setAnswer(new String(request.getParameter("answer").getBytes(
				"ISO-8859-1"), "GBK"));
		if (!q.getContext().equals(""))// �жϵõ��������Ƿ�Ϊ��;
		{
			System.out.print(b = qsi.save(q));// ����Ŀ���浽���ݿ���;
			if (b)// �ж�����Ƿ񱣴�ɹ�;
				request.setAttribute("qinfo", "��ӳɹ�");// �ڻỰ����ʾ��ӳɹ�;
			else
				request.setAttribute("qinfo", "���ʧ��");// ���� ��ʾ���ʧ��;
		}
		return mapping.findForward("save");// ��תҳ��
	}

	// ��ѯ������action
	public ActionForward seekScore(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		int score = 0;
		HttpSession hs = request.getSession(false); // �õ�һ���Ự�ռ�,
		String term = request.getParameter("select"); // ��ҳ���еõ�select����
		Student s = (Student) hs.getAttribute("st");// �ӻỰ�еõ�һ��ѧ����ʵ��;
		try {
			score = es.selectStudentScore(s.getId(), term).getScore(); // ����һЩ��̨�ķ�����ȡ��ѧ�����Եĳɼ�;
		} catch (Exception e) {
			request.setAttribute("scoreinfo", "�޳ɼ�");// �쳣�Ĵ���;
		}
		request.setAttribute("scoreinfo", score); // ��ȡ�õĳɼ��ŵ������ҳ����;
		return mapping.findForward("list"); // ʵ��ҳ�����ת;
	}
}
