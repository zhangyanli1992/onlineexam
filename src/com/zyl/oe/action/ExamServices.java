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
 * 考试的action用来控制考生考试的页面跳转,
 * 
 */
public class ExamServices extends MappingDispatchAction {
	// 创建考试的业务实例,,题库的业务实例,学生的时例,讲其放到全局的变量;
	ExamService es = new ExamServiceImpl();

	QuestionServiceIf qsi = new QuestionServiceImp();

	StudentService sts = new StudentServiceImp();

	// 在页面随机生成的业试卷action;
	public ActionForward exampaperList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		HttpSession hs = request.getSession(false);// ;判断是否存在,如果不存在返回空;
		String termnum = request.getParameter("term");// 从页面上读取学期参数;

		try {
			if (hs.getAttribute("qflg").equals("0"))// 判断
			{
				List<Question> eps = new ArrayList<Question>();
				eps = es.generatorPaper(20); // 生成一张最大为二十题目的试卷
				hs.setAttribute("eps", eps); // 将生成的试卷放到会话中;
				hs.setAttribute("qflg", "1");
				hs.setAttribute("term", termnum);
			}
		} catch (Exception e) {

			e.printStackTrace();
		}

		return mapping.findForward("list");// 页面的跳转;
	}

	// 成绩的判断
	public ActionForward graded(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String[] answers = ((ExampaperForm) form).getAnswers();// 通过表单来的到页面上输入的答案;
		Student s;
		HttpSession hs = request.getSession(false);
		List<ExamPaper> eps = new ArrayList<ExamPaper>();// 试卷的列表
		List<Question> qs = (List<Question>) hs.getAttribute("eps"); // 从会话中得到所有的问题;
		s = (Student) hs.getAttribute("st");// 得到会话中的学生对象;
		int i = 1;
		for (Question q : qs)// 迭代题目
		{
			// 并将会话中的问题添加到试卷中;
			ExamPaper ep = new ExamPaper();
			ep.setQuestion(q);
			ep.setAnswer(answers[i]);
			// System.out.println(answers[i]);
			i++;
			eps.add(ep);
		}
		// 调用相应的业务方法来进行评分;
		int erid = es.submit(s, eps);
		int score = es.autograde(erid);
		score *= 5;
		try {
			Examreport er = es.getExamreport(erid);// 得到此试卷的分数;
			er.setTerm((String) hs.getAttribute("term"));// 得到页面中的term参数;
			er.setScore(score);
			es.modify(er, score);
			request.setAttribute("score", score);// 将成绩放到当前的请求页面中;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapping.findForward("t");// 页面的跳转;
	}

	// 得到所有问题的列表;
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

	// 删除题目;
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

	// 添加题目;
	public ActionForward addQuestion(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		boolean b;
		Question q = new Question();
		q.setContext(new String(request.getParameter("context").getBytes(
				"ISO-8859-1"), "GBK")); // 是用来编码的转换
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
		if (!q.getContext().equals(""))// 判断得到的问题是否为空;
		{
			System.out.print(b = qsi.save(q));// 将题目保存到数据库中;
			if (b)// 判断如果是否保存成功;
				request.setAttribute("qinfo", "添加成功");// 在会话中显示添加成功;
			else
				request.setAttribute("qinfo", "添加失败");// 否则 显示添加失败;
		}
		return mapping.findForward("save");// 跳转页面
	}

	// 查询分数的action
	public ActionForward seekScore(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		int score = 0;
		HttpSession hs = request.getSession(false); // 得到一个会话空间,
		String term = request.getParameter("select"); // 从页面中得到select参数
		Student s = (Student) hs.getAttribute("st");// 从会话中得到一个学生的实体;
		try {
			score = es.selectStudentScore(s.getId(), term).getScore(); // 调用一些后台的方法来取得学生考试的成绩;
		} catch (Exception e) {
			request.setAttribute("scoreinfo", "无成绩");// 异常的处理;
		}
		request.setAttribute("scoreinfo", score); // 将取得的成绩放到请求的页面中;
		return mapping.findForward("list"); // 实现页面的跳转;
	}
}
