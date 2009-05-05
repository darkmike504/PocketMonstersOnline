package org.pokenet.client.ui.frames;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.pokenet.client.GameClient;
import org.pokenet.client.backend.Translator;

import mdes.slick.sui.Frame;
import mdes.slick.sui.TextArea;

/**
 * A window with information about the game
 * @author shadowkanji
 *
 */
public class AboutDialog extends Frame {
	private TextArea m_info;
	private Color m_bg, m_white;

	/**
	 * Default constructor
	 */
	public AboutDialog() {
		m_bg = new Color(0, 0, 0, 70);
		m_white = new Color(255, 255, 255);
		List<String> translated = new ArrayList<String>();
		translated = Translator.translate("_LOGIN");
		this.setTitle(translated.get(34));
		this.setLocation(128, 256);
		this.setBackground(m_bg);
		this.setResizable(false);
		
		m_info = new TextArea();
		m_info.setSize(280, 320);
		m_info.setLocation(4, 4);
		m_info.setWrapEnabled(true);
		m_info.setText(translated.get(35)+"\n"+
				translated.get(36)+"\n"+
				translated.get(37)+"\n"+
				translated.get(38)+"\n"+
				translated.get(39)+"\n");
		m_info.setFont(GameClient.getFontSmall());
		m_info.setBackground(m_bg);
		m_info.setForeground(m_white);
		this.add(m_info);
		
		this.setSize(288, 320);
		
		this.setVisible(false);
	}
}
