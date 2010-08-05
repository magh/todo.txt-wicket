package com.todotxt.todotxtwicket;

import java.util.List;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.todotxt.todotxtjava.Task;

@AuthorizeInstantiation("USER")
@SuppressWarnings("serial")
public class TaskListPage extends WebPage {

	private final static Logger log = LoggerFactory.getLogger(TaskListPage.class);

	public TaskListPage() {
		TodotxtSession session = (TodotxtSession) getSession();
		try {
			session.fetchTasks();
			TaskDataProvider provider = session.getTaskProvider();
			final DataView<Task> dataView = new DataView<Task>("sorting", provider) {
				@Override
				protected void populateItem(final Item<Task> item) {
					IModel<Task> model = item.getModel();
					item.add(new Label("prio", ""+model.getObject().prio));
					item.add(new Label("text", model.getObject().text));
					item.add(new Link<Task>("edit") {
						@Override
						public void onClick() {
							setResponsePage(new TaskEditPage(item.getModel()));
						}
					});
				}
			};

			dataView.setItemsPerPage(20);

			add(new OrderByBorder("orderByPrio", "prio", provider) {
				@Override
				protected void onSortChanged() {
					dataView.setCurrentPage(0);
				}
			});
			add(new OrderByBorder("orderByText", "text", provider) {
				@Override
				protected void onSortChanged() {
					dataView.setCurrentPage(0);
				}
			});

			add(dataView);

			add(new PagingNavigator("navigator", dataView));
			
			//add
			Form add = new Form("add") {
				@Override
				protected void onSubmit() {
					setResponsePage(TaskEditPage.class);
				}
			};
			add(add);
			//sync
			Form sync = new Form("sync") {
				@Override
				protected void onSubmit() {
					TodotxtSession session = (TodotxtSession) getSession();
					session.fetchTasks();
				}
			};
			add(sync);
			//logout
			Form logout = new Form("logout") {
				@Override
				protected void onSubmit() {
					TodotxtSession session = (TodotxtSession) getSession();
					session.signOut();
				}
			};
			add(logout);

			final Model<String> filters = new Model<String>();

			//search
			final TextField<String> search = new TextField<String>("search", new Model<String>());
			Form searchform = new Form("searchform") {
				@Override
				protected void onSubmit() {
					String filter = search.getDefaultModelObjectAsString();
					log.debug("searchform onSubmit: "+filter);
					TodotxtSession session = (TodotxtSession) getSession();
					List<String> fs = session.getTaskProvider().addFilter(filter);
					StringBuilder sb = new StringBuilder();
					for (String f : fs) {
						sb.append(" -> ");
						sb.append(f);
					}
					filters.setObject(sb.toString());
				}
			};
			searchform.add(search);
			add(searchform);
			
			Form filterform = new Form("filterform") {
				@Override
				protected void onSubmit() {
					log.debug("filterform onSubmit");
					TodotxtSession session = (TodotxtSession) getSession();
					session.getTaskProvider().clearFilters();
					filters.setObject("");
				}
			};
			filterform.add(new Label("filters", filters));
			add(filterform);
		} catch (Exception e) {
			//TODO test this; remove dropbox Todo.txt application and sync. 
			log.info("Failed to fetch todo file!" + e.getMessage(), e);
			session.initDropboxSupport();
		}
	}

	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
		log.info("onBeforeRender");
	}

}
