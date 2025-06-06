/******************************************************************************
 * Copyright (C) 2008 Low Heng Sin  All Rights Reserved.                      *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * Posterita Ltd., 3, Draper Avenue, Quatre Bornes, Mauritius                 *
 * or via info@posterita.org or http://www.posterita.org/                     *
 *****************************************************************************/

package org.adempiere.webui.window;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.logging.Level;

import org.adempiere.webui.AdempiereWebUI;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.component.ZkCssHelper;
import org.adempiere.webui.editor.WBinaryEditor;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.util.Icon;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.compiere.model.MSysConfig;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Center;
import org.zkoss.zul.North;
import org.zkoss.zul.South;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Iframe;


/**
 * Dialog to view, upload new, remove or download media.
 * @see WBinaryEditor 
 * @author Low Heng Sin
 *
 */
public class WMediaDialog extends Window implements EventListener<Event>
{
	/**
	 * generated serial id
	 */
	private static final long serialVersionUID = -329919930778203892L;

	private static final CLogger log = CLogger.getCLogger(WMediaDialog.class);

	/** data	*/
	private Object m_data;
	
	/** Change					*/
	private boolean m_change = false;

	private Iframe preview = new Iframe();

	private Button bDelete = new Button();
	private Button bSave = new Button();
	private Button bLoad = new Button();
	private Button bCancel = new Button();
	private Button bOk = new Button();

	private Panel previewPanel = new Panel();
	
	private Borderlayout mainPanel = new Borderlayout();

	private Hbox toolBar = new Hbox();	
	
	private Hbox confirmPanel = new Hbox();

	private boolean m_cancel;
	/* SysConfig USE_ESC_FOR_TAB_CLOSING */
	private boolean isUseEscForTabClosing = MSysConfig.getBooleanValue(MSysConfig.USE_ESC_FOR_TAB_CLOSING, false, Env.getAD_Client_ID(Env.getCtx()));

	/**
	 *	Constructor.
	 *  @param title
	 *  @param data
	 */	
	public WMediaDialog(String title, Object data)
	{
		super();
		this.setTitle(title);
		
		try
		{
			staticInit();
			m_data = data;
			displayData();
		}
		catch (Exception ex)
		{
			log.log(Level.SEVERE, "", ex);
		}		
	} // WMediaDialog

	/**
	 *	Layout dialog.
	 *  <pre>
	 *  - northPanel
	 *      - toolBar
	 *      - title
	 *  - centerPane [split]
	 * 		- graphPanel (left)
	 *		  	- gifScroll - gifPanel
	 *			- pdfViewer
	 *  	- text (right)
	 *  - confirmPanel
	 *  </pre>
	 *  @throws Exception
	 */
	
	protected void staticInit() throws Exception
	{
		if (!ThemeManager.isUseCSSForWindowSize())
		{
			ZKUpdateUtil.setWindowWidthX(this, 500);
			ZKUpdateUtil.setWindowHeightX(this, 500);
		}
		else
		{
			addCallback("afterPageAttached", t -> afterPageAttached());
		}
		this.setSclass("media-dialog");
		this.setClosable(true);
		this.setSizable(true);
		this.setMaximizable(true);
		this.setBorder("normal");
		this.appendChild(mainPanel);
		ZKUpdateUtil.setHeight(mainPanel, "100%");
		ZKUpdateUtil.setWidth(mainPanel, "100%");
				
		North northPanel = new North();
		northPanel.setCollapsible(false);
		northPanel.setSplittable(false);
		
		toolBar.appendChild(bLoad);
		toolBar.appendChild(bDelete);
		toolBar.appendChild(bSave);
		
		mainPanel.appendChild(northPanel);
		northPanel.appendChild(toolBar);
		
		bSave.setEnabled(false);
		if (ThemeManager.isUseFontIconForImage())
			bSave.setIconSclass(Icon.getIconSclass(Icon.EXPORT));
		else
			bSave.setImage(ThemeManager.getThemeResource("images/Download24.png"));
		bSave.setTooltiptext(Msg.getMsg(Env.getCtx(), "AttachmentSave"));
		bSave.addEventListener(Events.ON_CLICK, this);

		if (ThemeManager.isUseFontIconForImage())
			bLoad.setIconSclass(Icon.getIconSclass(Icon.IMPORT));
		else
			bLoad.setImage(ThemeManager.getThemeResource("images/Upload24.png"));
		bLoad.setTooltiptext(Msg.getMsg(Env.getCtx(), "Load"));
		bLoad.addEventListener(Events.ON_UPLOAD, this);
		bLoad.setUpload(AdempiereWebUI.getUploadSetting());

		if (ThemeManager.isUseFontIconForImage())
			bDelete.setIconSclass(Icon.getIconSclass(Icon.DELETE));
		else
			bDelete.setImage(ThemeManager.getThemeResource("images/Delete24.png"));
		bDelete.setTooltiptext(Msg.getMsg(Env.getCtx(), "Delete"));
		bDelete.addEventListener(Events.ON_CLICK, this);

		previewPanel.appendChild(preview);
		ZkCssHelper.appendStyle(previewPanel, "margin-top: 10px; margin-bottom: 10px;");
		ZKUpdateUtil.setHeight(preview, "100%");
		ZKUpdateUtil.setWidth(preview, "100%");
			
		Center centerPane = new Center();
		centerPane.setAutoscroll(true);
		ZKUpdateUtil.setHflex(previewPanel, "true");
		ZKUpdateUtil.setVflex(previewPanel, "true");
		mainPanel.appendChild(centerPane);
		centerPane.appendChild(previewPanel);
		
		South southPane = new South();
		mainPanel.appendChild(southPane);
		southPane.appendChild(confirmPanel);
		ZKUpdateUtil.setVflex(southPane, "min");		
		
		if(ThemeManager.isUseFontIconForImage())
			bOk.setIconSclass(Icon.getIconSclass(Icon.OK));
		else
			bOk.setImage(ThemeManager.getThemeResource("images/Ok24.png"));
		bOk.addEventListener(Events.ON_CLICK, this);
				
		if(ThemeManager.isUseFontIconForImage())
			bCancel.setIconSclass(Icon.getIconSclass(Icon.CANCEL));
		else
			bCancel.setImage(ThemeManager.getThemeResource("images/Cancel24.png"));
		bCancel.addEventListener(Events.ON_CLICK, this);
		
		confirmPanel.appendChild(bOk);
		confirmPanel.appendChild(bCancel);
		confirmPanel.setStyle("float: right;");
		addEventListener(Events.ON_CANCEL, e -> onCancel());
	}
	
	private void  afterPageAttached() {
		ZKUpdateUtil.setCSSHeight(this);
		ZKUpdateUtil.setCSSWidth(this);		
	}

	/**
	 * Close dialog
	 */	
	public void dispose ()
	{
		preview = null;
		this.detach();
	} // dispose
	
	/**
	 *  Display media
	 */	
	private void displayData ()
	{
		//	Reset UI		
		preview.setVisible(false);

		bDelete.setEnabled(false);
		bSave.setEnabled(false);

		if (m_data != null)
		{
			bSave.setEnabled(true);
			bDelete.setEnabled(true);
			
			try
			{
				AMedia media = createMedia();
				
				preview.setContent(media);
				preview.setClientAttribute("sandbox", "");
				preview.setVisible(true);
				preview.invalidate();
			}
			catch (Exception e)
			{
				log.log(Level.SEVERE, "Failed to preview content", e);
			}
		}		
	}   //  displayData

	/**
	 * Create media from {@link #m_data}
	 * @return media
	 * @throws SQLException
	 */
	private AMedia createMedia() throws SQLException {
		AMedia media;
		String contentType = null;
		if (m_data instanceof byte[])
		{
			media = new AMedia(this.getTitle(), null, contentType, (byte[])m_data);
		}
		else if (m_data instanceof Blob)
		{
			media = new AMedia(this.getTitle(), null, contentType, ((Blob)m_data).getBinaryStream());
		}
		else if (m_data instanceof Clob)
		{
			Clob clob = (Clob)m_data;
			long length = clob.length() > 100 ? 100 : clob.length();
			String data = ((Clob)m_data).getSubString(1, Long.valueOf(length).intValue());
			if (data.toUpperCase().indexOf("<html>") >= 0)
			{
				contentType = "text/html";
			}
			else
			{
				contentType = "text/plain";
			}
			media = new AMedia(this.getTitle(), null, contentType, ((Clob)m_data).getCharacterStream());
		}
		else
		{
			contentType = "text/plain";
			media = new AMedia(this.getTitle(), null, contentType, m_data.toString());
		}
		return media;
	}
	
	/**
	 *	Handle event
	 *  @param e event
	 */
	@Override
	public void onEvent(Event e)
	{
		//	Close		
		if (e.getTarget() == bOk)
		{
			dispose();
		}
	
		//	Cancel		
		else if (e.getTarget() == bCancel)
		{
			onCancel();
		}
		
		//	Clear data		
		else if (e.getTarget() == bDelete)
		{
			m_data = null;
			m_change = true;
			displayData();
		}
		
		//	Download		
		else if (e.getTarget() == bSave)
		{
			save();
		}
		
		// Upload new media
		else if (e instanceof UploadEvent)
		{
			UploadEvent ue = (UploadEvent) e;
			processUploadMedia(ue.getMedia());
		}
	}	//	onEvent

	/**
	 * Handle onCancel event
	 */
	private void onCancel() {
		// do not allow to close tab for Events.ON_CTRL_KEY event
		if(isUseEscForTabClosing)
			SessionManager.getAppDesktop().setCloseTabWithShortcut(false);

		m_cancel = true;
		dispose();
	}
	
	/**
	 * Process uploaded media
	 * @param media
	 */
	private void processUploadMedia(Media media) {
		if (media == null)
			return;
	
		String fileName = media.getName();
		if (log.isLoggable(Level.CONFIG))
			log.config(fileName);
		//update		
		m_change = true;
		m_data = media.getByteData();
		displayData();
	}

	/**
	 * download media
	 */	
	private void save()
	{
		if (m_data == null)
			return;

		try
		{
			AMedia media = createMedia();
			Filedownload.save(media);
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "Failed to export content.", e);
		}
	}
	
	/**
	 * @return true if cancel by user
	 */
	public boolean isCancel() {
		return m_cancel;
	}
	
	/**
	 * @return true if there's changes
	 */
	public boolean isChange() {
		return m_change;
	}
	
	/**
	 * @return data
	 */
	public Object getData() {
		return m_data;
	}

	@Override
	public void focus() {
		super.focus();
		bLoad.focus();
	}		
}
