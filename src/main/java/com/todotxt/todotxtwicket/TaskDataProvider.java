package com.todotxt.todotxtwicket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.todotxt.todotxtjava.Task;
import com.todotxt.todotxtjava.TaskHelper;

@SuppressWarnings("serial")
public class TaskDataProvider extends SortableDataProvider<Task> {
	
	private final static Logger log = LoggerFactory.getLogger(TaskDataProvider.class);

	private List<Task> tasks = new ArrayList<Task>();

	public TaskDataProvider() {
		setSort("text", true);
	}
	
	public void setTasks(List<Task> tasks){
		this.tasks = tasks;
	}

	public Iterator<Task> iterator(int first, int count) {
		SortParam sp = getSort();
		String property = sp.getProperty();
		Comparator<Task> cmp;
		log.debug("property=" + property + " first=" + first + " count="
				+ count);
		if("text".equals(property)){
			cmp = TaskHelper.byText;
		}else if("id".equals(property)){
			cmp = TaskHelper.byId;
//		}else if("prio".equals(property)){
//			cmp = TaskHelper.byPrio;
		}else{
			cmp = TaskHelper.byPrio;
		}
		if(sp.isAscending()){
			cmp = Collections.reverseOrder(cmp);
		}
		Collections.sort(tasks, cmp);
		List<Task> temp = tasks.subList(first, first+count);
		return temp.iterator();
	}

	public int size() {
		return tasks.size();
	}

	public IModel<Task> model(Task object) {
		return new Model<Task>(object);
	}

}
