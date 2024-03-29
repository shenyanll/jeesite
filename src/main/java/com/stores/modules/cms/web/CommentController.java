/**
 * Copyright &copy; 2012-2013 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.stores.modules.cms.web;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.stores.common.config.Global;
import com.stores.common.persistence.Page;
import com.stores.common.utils.StringUtils;
import com.stores.common.web.BaseController;
import com.stores.modules.cms.entity.Comment;
import com.stores.modules.cms.service.CommentService;
import com.stores.modules.sys.utils.DictUtils;
import com.stores.modules.sys.utils.UserUtils;

/**
 * 评论Controller
 * @author ThinkGem
 * @version 2013-3-23
 */
@Controller
@RequestMapping(value = Global.ADMIN_PATH+"/cms/comment")
public class CommentController extends BaseController {

	@Autowired
	private CommentService commentService;
	
	@ModelAttribute
	public Comment get(@RequestParam(required=false) Long id) {
		if (id != null){
			return commentService.get(id);
		}else{
			return new Comment();
		}
	}
	
	@RequiresPermissions("cms:comment:view")
	@RequestMapping(value = {"list", ""})
	public String list(Comment comment, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<Comment> page = commentService.find(new Page<Comment>(request, response), comment); 
        model.addAttribute("page", page);
		return "modules/cms/commentList";
	}

	@RequiresPermissions("cms:comment:edit")
	@RequestMapping(value = "save")
	public String save(Comment comment, RedirectAttributes redirectAttributes) {
		if (beanValidator(redirectAttributes, comment)){
			if (comment.getAuditUser() == null){
				comment.setAuditUser(UserUtils.getUser());
				comment.setAuditDate(new Date());
			}
			comment.setStatus(Comment.STATUS_RELEASE);
			commentService.save(comment);
			addMessage(redirectAttributes, DictUtils.getDictLabel(comment.getStatus(), "cms_status", "保存")
					+"评论'" + StringUtils.abbr(StringUtils.replaceHtml(comment.getContent()),50) + "'成功");
		}
		return "redirect:"+Global.ADMIN_PATH+"/cms/comment/?repage&status=2";
	}
	
	@RequiresPermissions("cms:comment:edit")
	@RequestMapping(value = "delete")
	public String delete(Long id, @RequestParam(required=false) Boolean isRe, RedirectAttributes redirectAttributes) {
		commentService.delete(id, isRe);
		addMessage(redirectAttributes, (isRe!=null&&isRe?"恢复审核":"删除")+"评论成功");
		return "redirect:"+Global.ADMIN_PATH+"/cms/comment/?repage&status=2";
	}

}
