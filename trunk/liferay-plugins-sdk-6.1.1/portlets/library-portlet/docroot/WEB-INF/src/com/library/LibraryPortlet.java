package com.library;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.PortletURLFactoryUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.slayer.model.LMSBook;
import com.slayer.service.LMSBookLocalServiceUtil;

/**
 * Portlet implementation class LibraryPortlet
 */
public class LibraryPortlet extends MVCPortlet {
	
	public void updateBook(ActionRequest actionRequest,
			ActionResponse actionResponse) 
					throws IOException, PortletException {
	
		String bookTitle = ParamUtil.getString(actionRequest, "bookTitle");
		String author = ParamUtil.getString(actionRequest, "author");
		
		long bookId = ParamUtil.getLong(actionRequest, "bookId");
		
		if (bookId > 0l) {
			modifyBook(bookId, bookTitle, author);
		} else {
			LMSBookLocalServiceUtil.insertBook(bookTitle, author);
		}
		
		// redirect after insert
		ThemeDisplay themeDisplay = 
				(ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
		
		PortletConfig portletConfig = 
				(PortletConfig) actionRequest.getAttribute("javax.portlet.config");
		
		String portletName = portletConfig.getPortletName();
		
		PortletURL successPageURL = PortletURLFactoryUtil.create(
				actionRequest,
				portletName + "_WAR_" + portletName + "portlet", 
				themeDisplay.getPlid(), 
				PortletRequest.RENDER_PHASE);
		
		successPageURL.setParameter("jspPage", LibraryConstants.PAGE_SUCCESS);
		actionResponse.sendRedirect(successPageURL.toString());
	}

	private void modifyBook(long bookId, String bookTitle, String author) {
	
		LMSBook lmsBook = null;
		try {
			lmsBook = LMSBookLocalServiceUtil.fetchLMSBook(bookId);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (Validator.isNotNull(lmsBook)) {
			lmsBook.setBookTitle(bookTitle);
			lmsBook.setAuthor(author);
			
			// update the book
			try {
				LMSBookLocalServiceUtil.updateLMSBook(lmsBook);
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void deleteBook(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {
	
		long bookId = ParamUtil.getLong(actionRequest, "bookId");
		
		if (bookId > 0l) { // valid bookId
			try {
				LMSBookLocalServiceUtil.deleteLMSBook(bookId);
			} catch (PortalException | SystemException e) {
				e.printStackTrace();
			}
		}

		// gracefully redirecting to the default list view
		String redirectURL = ParamUtil.getString(actionRequest, "redirectURL");
		actionResponse.sendRedirect(redirectURL);
	}
}