package org.eclipse.epsilon.emc.simulink.model.element;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.epsilon.emc.simulink.engine.MatlabEngine;
import org.eclipse.epsilon.emc.simulink.engine.MatlabException;
import org.eclipse.epsilon.emc.simulink.model.SimulinkModel;
import org.eclipse.epsilon.emc.simulink.util.SimulinkUtil;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;

public class SimulinkPort extends SimulinkBlockModelElement {

	private static final String PORT = "Port";
	
	public SimulinkPort(SimulinkModel model, MatlabEngine engine, Double handle) {
		super(model, engine, handle);
	}

	public List<SimulinkLine> getLines() throws EolRuntimeException {
		Object children, lines; 
		try {
			engine.eval("handle = ?;" + "lines = get_param(handle, 'Line'); "
					+ "children = get_param(lines, 'LineChildren');", this.handle);

			children = engine.getVariable("children");
			lines = engine.getVariable("lines");
		} catch (MatlabException e) {
			throw new EolRuntimeException(e.getMessage());
		}
		if (children != null) {
			return SimulinkUtil.getSimulinkLines(model, engine, children);
			//return model.getLines(children);
		} else {
			return SimulinkUtil.getSimulinkLines(model, engine, lines);
			//return model.getLines(lines);
		}
	}
	@Override
	public Collection<String> getAllTypeNamesOf() {
		return Arrays.asList(PORT);
	}

	@Override
	public boolean deleteElementInModel() throws EolRuntimeException {
		return false;
	}

	@Override
	public String getType() {
		return PORT; // FIXME Inport/Ouport?
	}

}
