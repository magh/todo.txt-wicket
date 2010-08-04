package com.todotxt.todotxtwicket;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
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
			
			Form add = new Form("add") {
				@Override
				protected void onSubmit() {
					setResponsePage(TaskEditPage.class);
				}
			};
			add(add);
			Form sync = new Form("sync") {
				@Override
				protected void onSubmit() {
					TodotxtSession session = (TodotxtSession) getSession();
					session.fetchTasks();
				}
			};
			add(sync);
		} catch (Exception e) {
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
