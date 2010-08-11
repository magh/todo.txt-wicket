package com.todotxt.todotxtwicket;

import java.util.List;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.todotxt.todotxtjava.Task;

@AuthorizeInstantiation("USER")
@SuppressWarnings("serial")
public class TaskListPage extends TodotxtBorderPage {

	private final static Logger log = LoggerFactory.getLogger(TaskListPage.class);

	public TaskListPage() {
		log.debug("<init>");
		TodotxtSession session = (TodotxtSession) getSession();
		//don't fetch since file might not be up to date after a add task. 
		int size = session.getTaskProvider().size();
		if(size <= 0){
			session.fetchTasks();
		}
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
		provider.setSort("prio", false);

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
		add(new Link<String>("add"){
			@Override
			public void onClick() {
				setResponsePage(TaskEditPage.class);
			}
		});
		//sync
		add(new Link<String>("sync"){
			@Override
			public void onClick() {
				TodotxtSession session = (TodotxtSession) getSession();
				session.fetchTasks();
			}
		});

		//search
		final TextField<String> search = new TextField<String>("search", new Model<String>());
		Form searchform = new Form("searchform") {
			@Override
			protected void onSubmit() {
				String filter = search.getDefaultModelObjectAsString();
				String[] split = filter.split(" ");
				log.debug("searchform onSubmit: "+filter);
				TodotxtSession session = (TodotxtSession) getSession();
				session.getTaskProvider().setFilter(split);
				search.setModelObject("");
			}
		};
		searchform.add(search);
		searchform.add(new Label("filter", new AbstractReadOnlyModel<String>() {
			@Override
			public String getObject() {
				TodotxtSession session = (TodotxtSession) getSession();
				List<String> fs = session.getTaskProvider().getFilters();
				StringBuilder sb = new StringBuilder();
				for (String f : fs) {
					sb.append(" -> ");
					sb.append(f);
				}
				return sb.toString();
			}
		}));
		add(searchform);
	}

	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
		log.info("onBeforeRender");
	}

}
