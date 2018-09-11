package org.eclipse.epsilon.eol.dt.launching.tabs;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.epsilon.common.dt.EpsilonPlugin;
import org.eclipse.epsilon.common.dt.launching.tabs.AbstractAdvancedConfigurationTab;
import org.eclipse.epsilon.eol.dt.EolPlugin;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class EolAdvancedConfigurationTab extends AbstractAdvancedConfigurationTab {

	@Override
	public EpsilonPlugin getPlugin() {
		return EolPlugin.getDefault();
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// TODO Implement ILaunchConfigurationTab.setDefaults
//		throw new UnsupportedOperationException("Unimplemented Method    ILaunchConfigurationTab.setDefaults invoked.");
	}


	@Override
	public void createAdditionalGroups(FillLayout parentLayout) {
		// TODO Implement AbstractAdvancedConfigurationTab.createAdditionalGroups
//		throw new UnsupportedOperationException("Unimplemented Method    AbstractAdvancedConfigurationTab.createAdditionalGroups invoked.");
	}

	@Override
	public void storeValuesInConfiguration(ILaunchConfiguration configuration) {
		// TODO Implement AbstractAdvancedConfigurationTab.storeValuesInConfiguration
//		throw new UnsupportedOperationException("Unimplemented Method    AbstractAdvancedConfigurationTab.storeValuesInConfiguration invoked.");
	}

	@Override
	public void populateFromConfiguration(ILaunchConfiguration configuration) throws CoreException {
		// TODO Implement AbstractAdvancedConfigurationTab.populateFromConfiguration
//		throw new UnsupportedOperationException("Unimplemented Method    AbstractAdvancedConfigurationTab.populateFromConfiguration invoked.");
	}
	
	@Override
	public List<String> getImplementations() {
		return Arrays.asList("Eol");
	}

}
