/******************************************************************************
  Copyright (C) 2017 Adaxa Pty Ltd. All Rights Reserved.                
  This program is free software; you can redistribute it and/or modify it    
  under the terms version 2 of the GNU General Public License as published   
  by the Free Software Foundation. This program is distributed in the hope   
  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *****************************************************************************/

package org.adempiere.webui.component;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

import org.adempiere.webui.ClientInfo;
import org.adempiere.webui.desktop.FavouriteController;
import org.adempiere.webui.exception.ApplicationException;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.util.Icon;
import org.compiere.model.MMenu;
import org.compiere.model.MToolBarButtonRestrict;
import org.compiere.model.MTreeNode;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Tree;
import org.zkoss.zul.TreeNode;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treecol;
import org.zkoss.zul.Treecols;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.event.TreeDataEvent;

/**
 * User Favorite Tree Model
 * 
 * @author Logilite Technologies
 * @since  June 20, 2017
 */
public class FavoriteSimpleTreeModel extends SimpleTreeModel implements EventListener<Event>, TreeitemRenderer<Object>
{
	/**
	 * generated serial id 
	 */
	private static final long			serialVersionUID		= 6950349031548896628L;
	private static final CLogger		LOG						= CLogger.getCLogger(FavoriteSimpleTreeModel.class);

	public static final String			MOBILE_TOOLBAR_CTX_MENU	= "MobileFavCtxMenu";

	private List<EventListener<Event>>	onDropListners			= new ArrayList<EventListener<Event>>();

	private boolean						itemDraggable;

	/**
	 * @param root
	 */
	public FavoriteSimpleTreeModel(DefaultTreeNode<Object> root)
	{
		super(root);
	}

	/**
	 * Tree Initialization
	 * 
	 * @param  tree
	 * @param  trxName
	 * @return         {@link FavoriteSimpleTreeModel}
	 */
	public static FavoriteSimpleTreeModel initADTree(Tree tree, String trxName)
	{
		FavouriteController controller = FavouriteController.getInstance(Executions.getCurrent().getDesktop().getSession());
		MTreeNode root = controller.getRootNode();
		//
		FavoriteSimpleTreeModel treeModel = FavoriteSimpleTreeModel.createFrom(root);
		treeModel.addOnDropEventListener(new ADTreeFavoriteOnDropListener(tree, treeModel));
		treeModel.setItemDraggable(true);

		if (tree.getTreecols() == null)
		{
			Treecols treeCols = new Treecols();
			tree.appendChild(treeCols);
			Treecol treeCol = new Treecol();
			treeCols.appendChild(treeCol);
		}

		tree.setPageSize(-1);

		try
		{
			tree.setItemRenderer(treeModel);
			tree.setModel(treeModel);
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "Failed to setup favourite tree");
		}

		controller.setTreeAndModel(treeModel, tree);

		return treeModel;
	} // initADTree

	/**
	 * Creating Tree hierarchy
	 * 
	 * @param  root MTreeNode
	 * @return      {@link FavoriteSimpleTreeModel}
	 */
	public static FavoriteSimpleTreeModel createFrom(MTreeNode root)
	{
		Enumeration<?> nodeEnum = root.children();
		DefaultTreeNode<Object> stRoot = new DefaultTreeNode<Object>(root, nodeEnum.hasMoreElements() ? new ArrayList<TreeNode<Object>>() : null);
		populate(stRoot, root);
		return new FavoriteSimpleTreeModel(stRoot);
	}// createFrom

	/**
	 * Populate Node
	 * 
	 * @param stNode Root DefaultTreeNode
	 * @param root Root MTreeNode
	 */
	private static void populate(DefaultTreeNode<Object> stNode, MTreeNode root)
	{
		Enumeration<?> nodeEnum = root.children();
		while (nodeEnum.hasMoreElements())
		{
			MTreeNode childNode = (MTreeNode) nodeEnum.nextElement();
			DefaultTreeNode<Object> stChildNode = childNode.getChildCount() > 0 ? new DefaultTreeNode<Object>(childNode, new ArrayList<TreeNode<Object>>())
																				: new DefaultTreeNode<Object>(childNode);

			stNode.getChildren().add(stChildNode);
			if (childNode.getChildCount() > 0)
			{
				populate(stChildNode, childNode);
			}
		}
	} // populate

	@Override
	public void render(Treeitem ti, Object node, int index)
	{
		DefaultTreeNode<?> stn = (DefaultTreeNode<?>) node;
		MTreeNode mNode = (MTreeNode) stn.getData();
		Treecell tc = new Treecell(Objects.toString(node));

		Treerow tr = null;
		if (ti.getTreerow() == null)
		{
			tr = new Treerow();
			tr.setParent(ti);
			if (isItemDraggable())
			{
				tr.setDraggable("true");
			}
			if (!onDropListners.isEmpty())
			{
				ti.getTreerow().addEventListener(Events.ON_CLICK, this);

				tr.setDroppable("true");
				tr.addEventListener(Events.ON_SELECT, this);
				tr.addEventListener(Events.ON_DROP, this);
				if (!ClientInfo.isMobile())
				{
					tr.addEventListener(Events.ON_RIGHT_CLICK, this);
				}
			}

			// default user action for Collapsed/Expanded folder tree
			ti.setOpen(!mNode.isCollapsible());
			ti.setTooltiptext(mNode.getDescription());

			//
			if (mNode.getColor() != null)
			{
				String hex = ZkCssHelper.createHexColorString(mNode.getColor());
				ZkCssHelper.appendStyle(tc, "color: #" + hex);
			}

			// Set Icon
			if (ThemeManager.isUseFontIconForImage())
				tc.setIconSclass(getIconSclass(mNode));
			else
				tc.setImage(ThemeManager.getThemeResource(getIconFile(mNode)));

			//
			if (mNode.isSummary())
			{
				tc.addSclass("fav-summary-folder");
			}

			//
			if (mNode.isWindow())
			{
				// Check Window access for ReadWrite & New Toolbar button
				if (!MToolBarButtonRestrict.isNewButtonRestricted(MMenu.get(mNode.getMenu_ID()).getAD_Window_ID()))
				{
					Toolbarbutton newBtn = new Toolbarbutton();
					newBtn.setTooltiptext(Util.cleanAmp(Msg.getMsg(Env.getCtx(), "New")));
					newBtn.addEventListener(Events.ON_CLICK, this);
					tc.appendChild(newBtn);

					if (ThemeManager.isUseFontIconForImage())
					{
						newBtn.setIconSclass(Icon.getIconSclass(Icon.NEW));
						newBtn.setSclass("new-toolbarbutton");
					}
					else
					{
						newBtn.setSclass("menu-href-newbtn");
						newBtn.setImage(ThemeManager.getThemeResource("images/New10.png"));
					}
				}
			}

			if (ClientInfo.isMobile())
			{
				Toolbarbutton btnContextMenu = new Toolbarbutton();
				btnContextMenu.setClass("fav-mobile-ctx-menu");
				btnContextMenu.setIconSclass(Icon.getIconSclass(Icon.MORE));
				btnContextMenu.setAttribute(MOBILE_TOOLBAR_CTX_MENU, true);
				btnContextMenu.addEventListener(Events.ON_CLICK, this);
				tc.appendChild(btnContextMenu);
			}
		}
		else
		{
			tr = ti.getTreerow();
			tr.getChildren().clear();
		}
		tc.setParent(tr);
		ti.setValue(node);
	} // render

	@Override
	public void onEvent(Event event) throws Exception
	{
		Component comp = event.getTarget();
		String eventName = event.getName();

		if (Events.ON_DROP.equals(eventName)	|| Events.ON_RIGHT_CLICK.equals(eventName)
			|| (Events.ON_CLICK.equals(eventName)	&& comp instanceof Toolbarbutton
				&& ((Toolbarbutton) event.getTarget()).getAttribute(FavoriteSimpleTreeModel.MOBILE_TOOLBAR_CTX_MENU) != null
				&& (boolean) ((Toolbarbutton) event.getTarget()).getAttribute(FavoriteSimpleTreeModel.MOBILE_TOOLBAR_CTX_MENU)))
		{
			for (EventListener<Event> listener : onDropListners)
			{
				listener.onEvent(event);
			}
		}
		// On click of menu to open that window
		else if (Events.ON_CLICK.equals(eventName) || Events.ON_SELECT.equals(eventName))
		{
			boolean newRecord = false;
			if (comp instanceof Toolbarbutton)
			{
				comp = comp.getParent().getParent();
				newRecord = true;
			}

			if (comp instanceof Treerow)
			{
				Treerow treerow = (Treerow) comp;
				Treeitem treeitem = (Treeitem) treerow.getParent();
				Object value = treeitem.getValue();

				DefaultTreeNode<?> dtNode = (DefaultTreeNode<?>) value;
				MTreeNode mtn = (MTreeNode) dtNode.getData();

				if (newRecord)
				{
					onNewRecord(mtn.getMenu_ID());
				}
				else if (!mtn.isSummary())
				{
					int menuID = mtn.getMenu_ID();
					SessionManager.getAppDesktop().onMenuSelected(menuID);
				}
			}
		}
	} // onEvent

	/**
	 * Add new node to tree
	 * @param newNode
	 */
	public void addNode(DefaultTreeNode<Object> newNode)
	{
		DefaultTreeNode<Object> root = (DefaultTreeNode<Object>) getRoot();
		root.getChildren().add(newNode);

		fireEvent(TreeDataEvent.INTERVAL_ADDED, getPath(root), root.getChildCount() - 1, root.getChildCount() - 1);
	} // addNode

	/**
	 * Get child node
	 * @param parent Parent node
	 * @param index Index of child node
	 * @return DefaultTreeNode
	 */
	public DefaultTreeNode<Object> getChild(DefaultTreeNode<Object> parent, int index)
	{
		return (DefaultTreeNode<Object>) (parent).getChildAt(index);
	} // getChild

	/**
	 * Open window to create new record
	 * @param menuID
	 */
	private void onNewRecord(int menuID)
	{
		try
		{
			SessionManager.getAppDesktop().onNewRecord(menuID);
		}
		catch (Exception e)
		{
			throw new ApplicationException(e.getMessage(), e);
		}
	} // onNewRecord

	/**
	 * @param listener
	 */
	public void addOnDropEventListener(EventListener<Event> listener)
	{
		onDropListners.add(listener);
	}

	/**
	 * @param isDraggable
	 */
	public void setItemDraggable(boolean isDraggable)
	{
		itemDraggable = isDraggable;
	}

	/**
	 * @return true if item is draggable, false otherwise
	 */
	public boolean isItemDraggable()
	{
		return itemDraggable;
	}

	/**
	 * Get icon image url for tree node (folder, window, report, etc)
	 * @param mt MTreeNode
	 * @return icon image url
	 */
	private static String getIconFile(MTreeNode mt)
	{
		if (mt.isSummary())
			return "images/Folder16.png";
		if (mt.isWindow())
			return "images/mWindow.png";
		if (mt.isReport())
			return "images/mReport.png";
		if (mt.isTask() || mt.isProcess())
			return "images/mProcess.png";
		if (mt.isWorkFlow())
			return "images/mWorkFlow.png";
		if (mt.isForm())
			return "images/mForm.png";
		if (mt.isInfo())
			return "images/mInfo.png";
		return "images/mWindow.png";
	}

	/**
	 * Get font icon sclass for tree node (summary, window, report, etc)
	 * @param mt
	 * @return font icon sclass
	 */
	private static String getIconSclass(MTreeNode mt)
	{
		if (mt.isSummary())
			return Icon.getIconSclass(Icon.FOLDER);
		if (mt.isWindow())
			return Icon.getIconSclass(Icon.WINDOW);
		if (mt.isReport())
			return Icon.getIconSclass(Icon.REPORT);
		if (mt.isProcess())
			return Icon.getIconSclass(Icon.PROCESS);
		if (mt.isTask())
			return Icon.getIconSclass(Icon.TASK);
		if (mt.isWorkFlow())
			return Icon.getIconSclass(Icon.WORKFLOW);
		if (mt.isForm())
			return Icon.getIconSclass(Icon.FORM);
		if (mt.isInfo())
			return Icon.getIconSclass(Icon.INFO);
		return Icon.getIconSclass(Icon.WINDOW);
	} // getIconSclass

}
