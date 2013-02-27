/*
 * Copyright 2012 Vincent Demeester<vincent+shortbrain@demeester.fr>.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.shortbrain.vaadin.container;

import static org.shortbrain.vaadin.container.ContainerUtils.addContainerProperty;
import static org.shortbrain.vaadin.container.ContainerUtils.initContainer;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.shortbrain.vaadin.container.property.PropertyMetadata;
import org.shortbrain.vaadin.container.property.PropertyReaderAlgorithm;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Container.Hierarchical;
import com.vaadin.data.Item;

/**
 * Default abstract implementation of {@link ContainerFactory}.
 * 
 * @author Vincent Demeester <vincent@demeester.fr>
 * 
 * @param <BEAN>
 *            type of the beans.
 */
public abstract class AbstractContainerFactory<BEAN> extends
		ContainerFactory<BEAN> {

	/**
	 * Default BEAN property name.
	 */
	private static final String DEFAULT_BEAN_PROPERTY = "bean";

	/**
	 * Type of the bean.
	 */
	private Class<? extends BEAN> beanClass;

	/**
	 * The property reader algorithm.
	 */
	private PropertyReaderAlgorithm propertyReaderAlgorithm;

	/**
	 * The name of the property that holds the bean.
	 */
	private String beanProperty = DEFAULT_BEAN_PROPERTY;

	/**
	 * Creates an AbstractContainerFactory.
	 * 
	 * @param beanClass
	 *            the type of BEAN.
	 * @param propertyReaderAlgorithm
	 *            the algorithm to be used to get properties.
	 * @throws IllegalArgumentException
	 *             if beanClass or propertyReaderAlgorithm are null.
	 */
	public AbstractContainerFactory(Class<? extends BEAN> beanClass,
			PropertyReaderAlgorithm propertyReaderAlgorithm) {
		if (beanClass == null || propertyReaderAlgorithm == null) {
			throw new IllegalArgumentException(
					"beanClass and propertyReaderAlgorithm cannot be null.");
		}
		this.beanClass = beanClass;
		this.propertyReaderAlgorithm = propertyReaderAlgorithm;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * If container is null, the default type will be {@link Filterable}
	 */
	@Override
	public Container getContainerFromList(Container container, List<BEAN> beans) {
		return getContainerFromCollection(container, beans);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Container getContainerFromList(List<BEAN> beans,
			Class<? extends Container> containerClass) {
		return getContainerFromCollection(beans, containerClass);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Container getContainerFromList(Container container,
			List<BEAN> beans, Class<? extends Container> containerClass) {
		return getContainerFromCollection(container, beans, containerClass);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * If container is null, the default type will be {@link Filterable}
	 */
	@Override
	public Container getContainerFromCollection(Container container,
			Collection<BEAN> beans) {
		Class<? extends Container> containerClass = (container != null) ? container
				.getClass() : Filterable.class;
		return getContainerFromCollection(container, beans, containerClass);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Container getContainerFromCollection(Collection<BEAN> beans,
			Class<? extends Container> containerClass) {
		return getContainerFromCollection(null, beans, containerClass);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Container getContainerFromCollection(Container container,
			Collection<BEAN> beans, Class<? extends Container> containerClass) {
		try {
			// Initialize the container if null
			if (container == null) {
				container = initContainer(containerClass);
			}
			// Property
			List<PropertyMetadata> properties = updateProperties(container);
			// Clean it
			if (container.removeAllItems()) {
				populateContainer(container, properties, beans);
			}
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
		}
		return container;
	}

	/**
	 * Create or update properties of the given container.
	 * 
	 * @param container
	 *            the container to be updated.
	 */
	private List<PropertyMetadata> updateProperties(Container container) {
		List<PropertyMetadata> properties = propertyReaderAlgorithm
				.getProperties(beanClass);
		Collection<?> containerProperties = container.getContainerPropertyIds();
		for (PropertyMetadata property : properties) {
			if (!containerProperties.contains(property.getPropertyName())) {
				addContainerProperty(container, property);
			}
		}
		// Add a bean property at the end.
		addContainerProperty(container, getBeanProperty(), beanClass, null);
		return properties;
	}

	/**
	 * Populate the given container with the list of BEAN.
	 * 
	 * There is a few different case to handle. An example is a
	 * {@link Hierarchical} containers, that could have children, so this method
	 * needs a way to identify the children, etc…
	 * 
	 * Notes : the default behavior for now is to look for an attribute
	 * <code>children</code> of the BEAN object. But in the future it will need
	 * to support more flexible way to know how to handle specific case.
	 * 
	 * @param container
	 *            the container to be populated.
	 * @param properties
	 *            the properties.
	 * @param beans
	 *            the list of beans.
	 */
	private void populateContainer(Container container,
			List<PropertyMetadata> properties, Collection<BEAN> beans) {
		if (beans != null) {
			if (container instanceof Hierarchical) {
				for (BEAN bean : beans) {
					addHierarchicalItem((Hierarchical) container, properties,
							bean, null);
				}
			} else {
				for (BEAN bean : beans) {
					addItem(container, properties, bean, true);
				}
			}
		}
	}

	/**
	 * Add an item (using bean) to the container, and look for
	 * <code>children</code> if needed.
	 * 
	 * @param container
	 *            the container.
	 * @param properties
	 *            the properties.
	 * @param bean
	 *            the bean to add.
	 * @param introspect
	 *            introspect for children.
	 * @return the id of the added item
	 */
	@SuppressWarnings("unchecked")
	protected Object addItem(Container container,
			List<PropertyMetadata> properties, BEAN bean, boolean introspect) {
		Object itemId = container.addItem();
		for (PropertyMetadata metadata : properties) {
			String propertyId = metadata.getPropertyName();
			Object value = null;
			try {
				value = PropertyUtils.getProperty(bean,
						metadata.getPropertyAttribute());
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			container.getContainerProperty(itemId, propertyId).setValue(value);
		}
		// At the end, add the bean to the container (just in case)
		container.getContainerProperty(itemId, getBeanProperty())
				.setValue(bean);
		if (introspect) {
			Collection<BEAN> children;
			try {
				children = (Collection<BEAN>) PropertyUtils.getProperty(bean,
						"children");
				if (children != null && !children.isEmpty()) {
					for (BEAN child : children) {
						addItem(container, properties, child, introspect);
					}
				}
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
			} catch (RuntimeException e) {
				// FIXME This is evil (but I need this temporarly for hibernate
				// lazyInitialisation)
				// e.printStackTrace();
			}
		}
		return itemId;
	}

	/**
	 * Add a hierarchical item (using bean) to the container.
	 * 
	 * @param container
	 *            the container.
	 * @param properties
	 *            the properties.
	 * @param bean
	 *            the bean to add.
	 * @param parentId
	 *            the parent of the bean.
	 */
	@SuppressWarnings("unchecked")
	protected void addHierarchicalItem(Hierarchical container,
			List<PropertyMetadata> properties, BEAN bean, Object parentId) {
		Object itemId = addItem(container, properties, bean, false);
		if (parentId != null) {
			// Parent id can have children.
			container.setChildrenAllowed(parentId, true);
			// Set the parent for the current id.
			container.setParent(itemId, parentId);
			// Set no children by default
			container.setChildrenAllowed(itemId, false);
		}
		Collection<BEAN> children;
		try {
			children = (Collection<BEAN>) PropertyUtils.getProperty(bean,
					"children");
			if (children != null && !children.isEmpty()) {
				for (BEAN child : children) {
					addHierarchicalItem(container, properties, child, itemId);
				}
			}
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
		} catch (RuntimeException e) {
			// FIXME This is evil (but I need this temporarly for hibernate
			// lazyInitialisation)
			// e.printStackTrace();
		}
	}

	/**
	 * Get the name of the bean property.
	 * 
	 * @return the name of the property that holds the bean
	 */
	public String getBeanProperty() {
		return beanProperty;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setBeanProperty(String name) {
		if (beanProperty == null || beanProperty.equals("")) {
			this.beanProperty = DEFAULT_BEAN_PROPERTY;
		} else {
			this.beanProperty = name;
		}
	}

    @Override
    public Item newItem(BEAN bean) {
        return null;
    }

}
