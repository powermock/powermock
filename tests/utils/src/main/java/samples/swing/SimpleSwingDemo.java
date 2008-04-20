/*
 * Copyright 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package samples.swing;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * A basic swing application example.
 * 
 * @author Johan Haleby
 */
public class SimpleSwingDemo extends JFrame {

	private static final long serialVersionUID = -190175253588111657L;

	public SimpleSwingDemo() {
		initialize();
	}

	private void initialize() {
		setLayout(new FlowLayout());
		final JLabel jlbHelloWorld = new JLabel("Hello World!");
		JButton jButton = new JButton("Click Me!");

		jButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionevent) {
				jlbHelloWorld.setText("Clicked on button");
			}
		});

		add(jlbHelloWorld);
		add(jButton);
		setSize(100, 100);
		setVisible(true);
	}

	public static void main(String[] args) {
		new SimpleSwingDemo();
	}
}
