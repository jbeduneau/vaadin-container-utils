package org.shortbrain.vaadin.container;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Container.Hierarchical;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.util.IndexedContainer;

/**
 * Test class for ContainerUtils.
 * 
 * @author Vincent Demeester <vincent+work@demeester.fr>
 * 
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class ContainerUtilsTest {

	@Test
	public void initContainerNonSupported() throws InstantiationException {
		try {
			ContainerUtils.initContainer(NonSupportedContainer.class);
			fail("Should throw an InstantiationException");
		} catch (IllegalAccessException e) {
			fail("Should not throw an IllegalAccessException");
		} catch (InstantiationException e) {
			assertTrue("Should throw an InstantiationException",
					e instanceof InstantiationException);
		}
	}

	@Test
	public void initContainerFilterable() throws InstantiationException {
		try {
			Container container = ContainerUtils
					.initContainer(Filterable.class);
			assertNotNull("container should not be null", container);
			assertTrue("container should be a Filterable",
					container instanceof Filterable);
			assertEquals("container should be an IndexedContainer",
					IndexedContainer.class, container.getClass());
		} catch (IllegalAccessException e) {
			fail("Should not throw an IllegalAccessException");
		} catch (InstantiationException e) {
			fail("Should not throw an InstantiationException");
		}
	}

	@Test
	public void initContainerIndexed() throws InstantiationException {
		try {
			Container container = ContainerUtils
					.initContainer(IndexedContainer.class);
			assertNotNull("container should not be null", container);
			assertTrue("container should be a Filterable",
					container instanceof Filterable);
			assertEquals("container should be an IndexedContainer",
					IndexedContainer.class, container.getClass());
		} catch (IllegalAccessException e) {
			fail("Should not throw an IllegalAccessException");
		} catch (InstantiationException e) {
			fail("Should not throw an InstantiationException");
		}
	}

	@Test
	public void initContainerHierarchical() throws InstantiationException {
		try {
			Container container = ContainerUtils
					.initContainer(Hierarchical.class);
			assertNotNull("container should not be null", container);
			assertTrue("container should be a Hierarchical",
					container instanceof Hierarchical);
			assertEquals("container should be an HierarchicalContainer",
					HierarchicalContainer.class, container.getClass());
		} catch (IllegalAccessException e) {
			fail("Should not throw an IllegalAccessException");
		} catch (InstantiationException e) {
			fail("Should not throw an InstantiationException");
		}
	}

	@Test
	public void initContainerHierarchicalContainer()
			throws InstantiationException {
		try {
			Container container = ContainerUtils
					.initContainer(HierarchicalContainer.class);
			assertNotNull("container should not be null", container);
			assertTrue("container should be a Hierarchical",
					container instanceof Hierarchical);
			assertEquals("container should be an HierarchicalContainer",
					HierarchicalContainer.class, container.getClass());
		} catch (IllegalAccessException e) {
			fail("Should not throw an IllegalAccessException");
		} catch (InstantiationException e) {
			fail("Should not throw an InstantiationException");
		}
	}

	@Test
	public void addContainerPropertyMetadataNull() {
		try {
			ContainerUtils.addContainerProperty(null, null);
			fail("It should throw a NullPointerException");
		} catch (NullPointerException e) {
			assertTrue("Should throw an NullPointerException",
					e instanceof NullPointerException);
		}
		try {
			ContainerUtils.addContainerProperty(null, new PropertyMetadata(
					"propertyName", String.class, null, "propertyName"));
			fail("It should throw a NullPointerException");
		} catch (NullPointerException e) {
			assertTrue("Should throw an NullPointerException",
					e instanceof NullPointerException);
		}
		try {
			ContainerUtils.addContainerProperty(new IndexedContainer(), null);
			fail("It should throw a NullPointerException");
		} catch (NullPointerException e) {
			assertTrue("Should throw an NullPointerException",
					e instanceof NullPointerException);
		}
	}

	@Test
	public void addContainerPropertyMetadata() {
		Container container = new IndexedContainer();
		PropertyMetadata metadata = new PropertyMetadata("propertyName",
				String.class, null, "propertyName");
		ContainerUtils.addContainerProperty(container, metadata);
		assertTrue(
				"container should contains a property 'propertyName'",
				container.getContainerPropertyIds().contains(
						metadata.getPropertyName()));
		assertEquals(String.class, container.getType("propertyName"));
	}

	@Test
	public void addContainerPropertyNull() {
		try {
			ContainerUtils.addContainerProperty(null, null, null, null);
			fail("It should throw a NullPointerException");
		} catch (NullPointerException e) {
			assertTrue("Should throw an NullPointerException",
					e instanceof NullPointerException);
		}
		try {
			ContainerUtils.addContainerProperty(null, "propertyName",
					String.class, null);
			fail("It should throw a NullPointerException");
		} catch (NullPointerException e) {
			assertTrue("Should throw an NullPointerException",
					e instanceof NullPointerException);
		}
		try {
			ContainerUtils.addContainerProperty(new IndexedContainer(), null,
					null, null);
			fail("It should throw a NullPointerException");
		} catch (NullPointerException e) {
			assertTrue("Should throw an NullPointerException",
					e instanceof NullPointerException);
		}
		try {
			ContainerUtils.addContainerProperty(new IndexedContainer(),
					"nullProperty", null, null);
			fail("It should throw a NullPointerException");
		} catch (NullPointerException e) {
			assertTrue("Should throw an NullPointerException",
					e instanceof NullPointerException);
		}
	}

	@Test
	public void addContainerProperty() {
		Container container = new IndexedContainer();
		PropertyMetadata metadata = new PropertyMetadata("propertyName",
				String.class, null, "propertyName");
		ContainerUtils.addContainerProperty(container,
				metadata.getPropertyName(), metadata.getPropertyClass(),
				metadata.getDefaultValue());
		assertTrue(
				"container should contains a property 'propertyName'",
				container.getContainerPropertyIds().contains(
						metadata.getPropertyName()));
		assertEquals(String.class, container.getType("propertyName"));
	}

	/**
	 * Inner interface that define a non-support Container interface.
	 * 
	 * @author Vincent Demeester <vincent+work@demeester.fr>
	 * 
	 */
	private interface NonSupportedContainer extends Container {
	}
}
