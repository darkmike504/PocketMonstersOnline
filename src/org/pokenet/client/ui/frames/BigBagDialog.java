package org.pokenet.client.ui.frames;

import java.util.ArrayList;
import java.util.HashMap;

import mdes.slick.sui.Button;
import mdes.slick.sui.Frame;
import mdes.slick.sui.Label;
import mdes.slick.sui.event.ActionEvent;
import mdes.slick.sui.event.ActionListener;
import mdes.slick.sui.event.MouseAdapter;
import mdes.slick.sui.event.MouseEvent;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.loading.LoadingList;
import org.pokenet.client.GameClient;
import org.pokenet.client.backend.entity.PlayerItem;
import org.pokenet.client.ui.base.ImageButton;

/**
 * The big bag dialog
 * 
 * @author Nushio
 * @author ZombieBear
 * 
 */
public class BigBagDialog extends Frame {
	protected ImageButton[] m_categoryButtons;
	protected ArrayList<Button> m_itemBtns = new ArrayList<Button>();
	protected ArrayList<Label> m_stockLabels = new ArrayList<Label>();
	protected Button m_leftButton, m_rightButton, m_cancel;
	protected ItemPopup m_popup;

	private HashMap<Integer, ArrayList<PlayerItem>> m_items = new HashMap<Integer, ArrayList<PlayerItem>>();
	private HashMap<Integer, Integer> m_scrollIndex = new HashMap<Integer, Integer>();
	protected int m_curCategory = 0;
	protected boolean m_update = false;

	public BigBagDialog() {
		getContentPane().setX(getContentPane().getX() - 1);
		getContentPane().setY(getContentPane().getY() + 1);
		setCenter();
		initGUI();

		// Load the player's items and sort them by category
		for (PlayerItem item : GameClient.getInstance().getOurPlayer().getItems()) {
			// Field items
			if (item.getItem().getCategory().equalsIgnoreCase("Field") ||
					item.getItem().getCategory().equalsIgnoreCase("Evolution")) {
				m_items.get(0).add(item);
			}
			// Potions and medicine
			else if (item.getItem().getCategory().equalsIgnoreCase("Potions")
					|| item.getItem().getCategory().equalsIgnoreCase(
					"Medicine")) {
				m_items.get(1).add(item);
			}
			// Berries and food
			else if (item.	getItem().getCategory().equalsIgnoreCase("Food")) {
				m_items.get(2).add(item);
			}
			// Pokeballs
			else if (item.getItem().getCategory().equalsIgnoreCase("Pokeball")) {
				m_items.get(3).add(item);
			}
			// TMs
			else if (item.getItem().getCategory().equalsIgnoreCase("TM")) {
				m_items.get(4).add(item);
			}
		}
		m_update = true;
	}

	/**
	 * Initializes the interface
	 */
	public void initGUI() {
		m_categoryButtons = new ImageButton[5];

		for (int i = 0; i < m_categoryButtons.length; i++) {
			final int j = i;
			try {
				Image[] bagcat = new Image[] {
						new Image("res/ui/bag/bag_normal.png"),
						new Image("res/ui/bag/bag_hover.png"),
						new Image("res/ui/bag/bag_pressed.png") };
				Image[] potioncat = new Image[] {
						new Image("res/ui/bag/potions_normal.png"),
						new Image("res/ui/bag/potions_hover.png"),
						new Image("res/ui/bag/potions_pressed.png") };
				Image[] berriescat = new Image[] {
						new Image("res/ui/bag/berries_normal.png"),
						new Image("res/ui/bag/berries_hover.png"),
						new Image("res/ui/bag/berries_pressed.png") };
				Image[] pokecat = new Image[] {
						new Image("res/ui/bag/pokeballs_normal.png"),
						new Image("res/ui/bag/pokeballs_hover.png"),
						new Image("res/ui/bag/pokeballs_pressed.png") };
				Image[] tmscat = new Image[] {
						new Image("res/ui/bag/tms_normal.png"),
						new Image("res/ui/bag/tms_hover.png"),
						new Image("res/ui/bag/tms_pressed.png") };

				switch (i) {
				case 0:
					m_categoryButtons[i] = new ImageButton(bagcat[0],
							bagcat[1], bagcat[2]);
					break;
				case 1:
					m_categoryButtons[i] = new ImageButton(potioncat[0],
							potioncat[1], potioncat[2]);
					break;
				case 2:
					m_categoryButtons[i] = new ImageButton(berriescat[0],
							berriescat[1], berriescat[2]);
					break;
				case 3:
					m_categoryButtons[i] = new ImageButton(pokecat[0],
							pokecat[1], pokecat[2]);
					break;
				case 4:
					m_categoryButtons[i] = new ImageButton(tmscat[0],
							tmscat[1], tmscat[2]);
					break;
				}

				m_items.put(i, new ArrayList<PlayerItem>());
				m_scrollIndex.put(i, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			m_categoryButtons[i].setSize(40, 40);
			if (i == 0)
				m_categoryButtons[i].setLocation(80, 10);
			else
				m_categoryButtons[i].setLocation(m_categoryButtons[i - 1]
						.getX() + 65, 10);
			m_categoryButtons[i].setFont(GameClient.getFontLarge());
			m_categoryButtons[i].setOpaque(false);
			m_categoryButtons[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					destroyPopup();
					m_curCategory = j;
					m_update = true;
				}
			});
			getContentPane().add(m_categoryButtons[i]);
		}

		// Bag Image
		Label bagicon = new Label("");
		bagicon.setSize(40, 40);

		LoadingList.setDeferredLoading(true);
		try {
			bagicon.setImage(new Image("res/ui/bag/front.png"));
		} catch (SlickException e1) {
		}
		LoadingList.setDeferredLoading(false);

		bagicon.setLocation(18, 0);
		bagicon.setFont(GameClient.getFontLarge());
		getContentPane().add(bagicon);

		// Scrolling Button LEFT
		m_leftButton = new Button("<");
		m_leftButton.setSize(20, 40);
		m_leftButton.setLocation(15, 95);
		m_leftButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				destroyPopup();
				int i = m_scrollIndex.get(m_curCategory) - 1;
				m_scrollIndex.remove(m_curCategory);
				m_scrollIndex.put(m_curCategory, i);
				m_update = true;
			}
		});
		getContentPane().add(m_leftButton);

		// Item Buttons and Stock Labels
		for (int i = 0; i < 4; i++) {
			final int j = i;
			// Starts the item buttons
			Button item = new Button();
			item.setSize(60, 60);
			item.setLocation(50 + (80 * i), 85);
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					destroyPopup();
					useItem(j);
				}
			});
			m_itemBtns.add(item);
			getContentPane().add(item);

			// Starts the item labels
			Label stock = new Label();
			stock.setSize(60, 40);
			stock.setLocation(50 + (80 * i), 135);
			stock.setHorizontalAlignment(Label.CENTER_ALIGNMENT);
			stock.setFont(GameClient.getFontLarge());
			stock.setForeground(Color.white);
			m_stockLabels.add(stock);
			getContentPane().add(stock);
		}

		// Scrolling Button Right
		m_rightButton = new Button(">");
		m_rightButton.setSize(20, 40);
		m_rightButton.setLocation(365, 95);
		m_rightButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				destroyPopup();
				int i = m_scrollIndex.get(m_curCategory) + 1;
				m_scrollIndex.remove(m_curCategory);
				m_scrollIndex.put(m_curCategory, i);
				m_update = true;
			}
		});
		getContentPane().add(m_rightButton);

		// Close Button
		m_cancel = new Button("Close");
		m_cancel.setSize(400, 32);
		m_cancel.setLocation(0, 195);
		m_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				destroyPopup();
				closeBag();
			}
		});
		getContentPane().add(m_cancel);

		// Frame properties
		getResizer().setVisible(false);
		getCloseButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				destroyPopup();
				closeBag();
			}
		});
		getContentPane().addMouseListener(new MouseAdapter() {
					@Override
					public void mouseReleased(MouseEvent e) {
						super.mouseReleased(e);
						destroyPopup();
					}
				});

		setBackground(new Color(0, 0, 0, 75));
		setTitle("Bag");
		setResizable(false);
		setHeight(250);
		setWidth(m_categoryButtons.length * 80);
		setVisible(true);
		setCenter();
	}

	/**
	 * Closes the bag
	 */
	public void closeBag() {
		setVisible(false);
		GameClient.getInstance().getDisplay().remove(this);
	}

	@Override
	public void update(GUIContext gc, int delta) {
		super.update(gc, delta);
		if (m_update) {
			m_update = false;
			// Enable/disable scrolling
			if (m_scrollIndex.get(m_curCategory) == 0)
				m_leftButton.setEnabled(false);
			else
				m_leftButton.setEnabled(true);
			
			if (m_scrollIndex.get(m_curCategory) + 4 >= m_items.get(m_curCategory).size())
				m_rightButton.setEnabled(false);
			else
				m_rightButton.setEnabled(true);
			
			// Update items and stocks
			for (int i = 0; i < 5; i++) {
				try {
					m_itemBtns.get(i).setName(
							String.valueOf(m_items.get(m_curCategory).get(
									m_scrollIndex.get(m_curCategory) + i)
									.getNumber()));
					m_itemBtns.get(i).setToolTipText(
							m_items.get(m_curCategory).get(
									m_scrollIndex.get(m_curCategory) + i)
									.getItem().getName());
					m_itemBtns.get(i).setImage(
							m_items.get(m_curCategory).get(
									m_scrollIndex.get(m_curCategory) + i)
									.getBagImage());
					m_stockLabels.get(i).setText(
							"x" + m_items.get(m_curCategory).get(
									m_scrollIndex.get(m_curCategory) + i)
									.getQuantity());
					m_itemBtns.get(i).setEnabled(true);
				} catch (Exception e) {
					m_itemBtns.get(i).setImage(null);
					m_itemBtns.get(i).setToolTipText("");
					m_itemBtns.get(i).setText("");
					m_stockLabels.get(i).setText("");
					m_itemBtns.get(i).setEnabled(false);
				}
			}
		}
	}
	
	/**
	 * An item was used!
	 * @param i
	 */
	public void useItem(int i) {
		destroyPopup();
		if (m_curCategory == 0 || m_curCategory == 3){
			m_popup = new ItemPopup(m_itemBtns.get(i).getToolTipText(), Integer.parseInt(
					m_itemBtns.get(i).getName()), false, false);
			m_popup.setLocation(m_itemBtns.get(i).getAbsoluteX(), m_itemBtns.get(i).getAbsoluteY() 
					+ m_itemBtns.get(i).getHeight() - getTitleBar().getHeight());
			getDisplay().add(m_popup);
		} else {
			m_popup = new ItemPopup(m_itemBtns.get(i).getToolTipText(), Integer.parseInt(
					m_itemBtns.get(i).getName()), true, false);
			m_popup.setLocation(m_itemBtns.get(i).getAbsoluteX(), m_itemBtns.get(i).getAbsoluteY() 
					+ m_itemBtns.get(i).getHeight() - getTitleBar().getHeight());
			getDisplay().add(m_popup);
		}
	}
	
	/**
	 * Destroys the item popup
	 */
	public void destroyPopup() {
		if (getDisplay().containsChild(m_popup)){
			m_popup.destroyPopup();
			m_popup = null;
		}
	}

	/**
	 * Centers the frame
	 */
	public void setCenter() {
		int height = (int) GameClient.getInstance().getDisplay().getHeight();
		int width = (int) GameClient.getInstance().getDisplay().getWidth();
		int x = (width / 2) - 200;
		int y = (height / 2) - 200;
		this.setBounds(x, y, this.getWidth(), this.getHeight());
	}
}

/**
 * The use dialog for items
 * @author ZombieBear
 *
 */
class ItemPopup extends Frame{
	private Label m_name;
	private Button m_use;
	private Button m_give;
	private Button m_destroy;
	private Button m_cancel;
	private TeamPopup m_team;
	
	/**
	 * Default Constructor
	 * @param item
	 * @param id
	 * @param useOnPokemon
	 * @param isBattle
	 */
	public ItemPopup(String item, int id, boolean useOnPokemon, boolean isBattle){
		final int m_id = id;
		final boolean m_useOnPoke = useOnPokemon;
		final boolean m_isBattle = isBattle;
		getContentPane().setX(getContentPane().getX() - 1);
		getContentPane().setY(getContentPane().getY() + 1);
		
		// Item name label
		m_name = new Label(item);
		m_name.setFont(GameClient.getFontSmall());
		m_name.setForeground(Color.white);
		m_name.pack();
		m_name.setLocation(0,0);
		getContentPane().add(m_name);
		
		// Use button
		m_use = new Button("Use");
		m_use.setSize(100,25);
		m_use.setLocation(0, m_name.getY() + m_name.getHeight() + 3);
		m_use.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				useItem(m_id, m_useOnPoke, m_isBattle);
			}
		});
		getContentPane().add(m_use);

		if (!isBattle){
			m_give = new Button("Give");
			m_give.setSize(100,25);
			m_give.setLocation(0, m_use.getY() + 25);
			m_give.setEnabled(false);
			m_give.addActionListener(new ActionListener(){
				public void actionPerformed (ActionEvent e){
					giveItem(m_id);
				}
			});
			getContentPane().add(m_give);
		}
		// Destroy the item
		m_destroy = new Button("Destroy");
		m_destroy.setSize(100,25);
		if (!isBattle)
			m_destroy.setLocation(0, m_give.getY() + 25);
		else
			m_destroy.setLocation(0, m_use.getY() + 25);
		m_destroy.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				destroyPopup();
			}
		});
		getContentPane().add(m_destroy);
		
		// Destroy the item
		m_cancel = new Button("Cancel");
		m_cancel.setSize(100,25);
		m_cancel.setLocation(0, m_destroy.getY() + 25);
		m_cancel.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){ 
				destroyPopup();
			}
		});
		getContentPane().add(m_cancel);
		
		// Frame configuration
		addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e) {
				destroyPopup();
			}
		});
		setBackground(new Color(0,0,0,150));
		if (!isBattle)
			setSize(100, 140);
		else
			setSize(100, 115);
		getTitleBar().setVisible(false);
		setVisible(true);
		setResizable(false);
		setAlwaysOnTop(true);
	}
	
	/**
	 * Destroys the popup
	 */
	public void destroyPopup() {
		getDisplay().remove(m_team);
		m_team = null;
		getDisplay().remove(this);
	}
	
	/**
	 * Use the item. usedOnPoke determine whether the item should be applied to a pokemon
	 * @param id
	 * @param usedOnPoke
	 */
	public void useItem(int id, boolean usedOnPoke, boolean isBattle){
		if (getDisplay().containsChild(m_team))
			getDisplay().remove(m_team);
		m_team = null;
		if (usedOnPoke) {
			setAlwaysOnTop(false);
			m_team = new TeamPopup(this, id, true, isBattle);
			m_team.setLocation(m_use.getAbsoluteX() + getWidth(), m_use.getAbsoluteY() - 15);
			getDisplay().add(m_team);
		} else {
			if (isBattle)
				GameClient.getInstance().getPacketGenerator().write("bi" + id);
			else
				GameClient.getInstance().getPacketGenerator().write("I" + id);
			destroyPopup();
		}
	}

	/**
	 * Give the item to a pokemon
	 * @param id
	 */
	public void giveItem(int id){
		setAlwaysOnTop(false);
		if (getDisplay().containsChild(m_team))
			getDisplay().remove(m_team);
		m_team = null;
		m_team = new TeamPopup(this, id, false, false);
		m_team.setLocation(m_give.getAbsoluteX() + getWidth(), m_give.getAbsoluteY() - 15);
		getDisplay().add(m_team);
	}
}

/**
 * PopUp that lists the player's pokemon in order to use/give an item
 * @author administrator
 *
 */
class TeamPopup extends Frame{
	ItemPopup m_parent;
	Label m_details;
	
	/**
	 * Default constructor
	 * @param itemId
	 * @param use
	 * @param useOnPoke
	 */
	public TeamPopup(ItemPopup parent, int itemId, boolean use, boolean isBattle) {
		getContentPane().setX(getContentPane().getX() - 1);
		getContentPane().setY(getContentPane().getY() + 1);

		m_parent = parent;
		final int m_item = itemId;
		final boolean m_use = use;
		final boolean m_isBattle = isBattle;

		int y = 0;
		for (int i = 0; i < GameClient.getInstance().getOurPlayer().getPokemon().length; i++) {
			try{
				final Label tempLabel = new Label(GameClient.getInstance().getOurPlayer().getPokemon()[i].getName());
				final int j = i;
				tempLabel.setSize(100, 15);
				tempLabel.setFont(GameClient.getFontSmall());
				tempLabel.setForeground(Color.white);
				tempLabel.setLocation(0, y);
				tempLabel.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseReleased(MouseEvent e) {
						super.mouseReleased(e);
						processItemUse(m_use, m_item, j, m_isBattle);
					}
					@Override
					public void mouseEntered(MouseEvent e) {
						super.mouseEntered(e);
						tempLabel.setForeground(new Color(255, 215, 0));
					}
					@Override
					public void mouseExited(MouseEvent e) {
						super.mouseExited(e);
						tempLabel.setForeground(new Color(255, 255, 255));
					}
				});
				y += 18;
				getContentPane().add(tempLabel);
			} catch (Exception e) {}
		}
		
		// Frame configuration
		setBackground(new Color(0,0,0,150));
		setSize(100, 115);
		getTitleBar().setVisible(false);
		setVisible(true);
		setResizable(false);
		setAlwaysOnTop(true);
	}
	
	public void processItemUse(boolean use, int id, int pokeIndex, boolean isBattle){
		if (use) {
			if (isBattle) {
				GameClient.getInstance().getPacketGenerator().write("bi" + id + "," + pokeIndex);
				GameClient.getInstance().getUi().getBattleManager().getBattleWindow().m_bag.closeBag();
			} else {
				GameClient.getInstance().getPacketGenerator().write("I" + id + "," + pokeIndex);
			}
		} else {
			// TODO: Write "Give" packet
			GameClient.getInstance().getPacketGenerator().write("");
		}
		m_parent.destroyPopup();
	}
}