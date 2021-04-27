/*
 *  Copyright 2021 Sanctum <https://github.com/the-h-team>
 *
 *  This file is part of RetroConomy.
 *
 *  This software is currently in development and its licensing has not
 *  yet been chosen.
 */
package com.github.sanctum.retro.construct.core;

import com.github.sanctum.labyrinth.data.container.DataContainer;
import com.github.sanctum.labyrinth.gui.InventoryRows;
import com.github.sanctum.labyrinth.gui.menuman.Menu;
import com.github.sanctum.labyrinth.gui.menuman.MenuBuilder;
import com.github.sanctum.labyrinth.gui.menuman.MenuClick;
import com.github.sanctum.labyrinth.gui.printer.AnvilBuilder;
import com.github.sanctum.labyrinth.gui.printer.AnvilMenu;
import com.github.sanctum.labyrinth.library.HUID;
import com.github.sanctum.labyrinth.library.Message;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.labyrinth.task.Schedule;
import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.util.Savable;
import com.github.sanctum.retro.util.TransactionType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.TileState;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class ATM extends Savable {

	private static final NamespacedKey KEY = new NamespacedKey(JavaPlugin.getProvidingPlugin(RetroConomy.class), "retro_atm_block");
	public static final Controller CONTROLLER = new Controller();
	private static final long serialVersionUID = -4263717446113447098L;

	private final OfflinePlayer owner;
	public final List<BankSlip> record = new ArrayList<>();
	private Location location = null;
	private final HUID id;
	private BigDecimal tax = BigDecimal.valueOf(2.13);

	private boolean locked;

	protected ATM(OfflinePlayer owner) {
		this.owner = owner;
		this.id = HUID.randomID();
		RetroConomy.getInstance().getManager().ATMS.add(this);
	}

	public static boolean has(OfflinePlayer target) {
		if (RetroConomy.getInstance().getManager().getATMs().list().stream().noneMatch(a -> a.getOwner().getUniqueId().equals(target.getUniqueId()))) {
			return false;
		}
		ATM atm = pick(target);
		return atm.getLocation() != null;
	}

	public static ATM pick(OfflinePlayer target) {
		return !RetroConomy.getInstance().getManager().getATMs().filter(atm -> atm.owner.getUniqueId().equals(target.getUniqueId())).findFirst().isPresent() ? new ATM(target) : RetroConomy.getInstance().getManager().getATMs().filter(atm -> atm.getOwner().getUniqueId().equals(target.getUniqueId())).findFirst().orElse(null);
	}

	public static ATM pick(Block b) {
		if (!(b.getState() instanceof TileState)) {
			return null;
		}
		TileState state = (TileState) b.getState();
		if (!state.getPersistentDataContainer().has(KEY, PersistentDataType.STRING)) {
			return null;
		}
		return RetroConomy.getInstance().getManager().getATMs().filter(a -> state.getPersistentDataContainer().get(KEY, PersistentDataType.STRING).equals(a.getOwner().getUniqueId().toString())).findFirst().orElse(null);
	}

	public void take(BankSlip slip) {
		if (!record.contains(slip)) {
			if (record.size() >= 100) {
				setLocked(true);
				return;
			}
			record.add(slip);
		}
	}

	@Override
	public HUID id() {
		RetroAccount account = RetroConomy.getInstance().getManager().getAccount(getOwner()).orElse(null);
		return account != null ? account.getId() : HUID.randomID();
	}

	@Override
	public ItemStack get() {
		ItemStack atm = new ItemStack(Material.CHEST);
		ItemMeta meta = atm.getItemMeta();
		meta.setDisplayName(StringUtils.use("&6[ATM] &e" + getOwner().getName()).translate());
		meta.setLore(Collections.singletonList(StringUtils.use("").translate()));
		atm.setItemMeta(meta);
		return atm;
	}

	public OfflinePlayer getOwner() {
		return owner;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public int despawn() {
		int count = 0;
		Chunk c = getLocation().getChunk();
		if (c.isLoaded()) {
			for (Entity e : c.getEntities()) {
				if (e instanceof ArmorStand) {
					ArmorStand a = (ArmorStand) e;
					if (a.isCustomNameVisible()) {
						if (a.getCustomName().equals(StringUtils.use("&6[ATM] &e" + getOwner().getName()).translate())) {
							a.remove();
							count++;
						}
					}
				}
			}
		} else {
			c.load();
			for (Entity e : c.getEntities()) {
				if (e instanceof ArmorStand) {
					ArmorStand a = (ArmorStand) e;
					if (a.isCustomNameVisible()) {
						if (a.getCustomName().equals(StringUtils.use("&6[ATM] &e" + getOwner().getName()).translate())) {
							a.remove();
							count++;
						}
					}
				}
			}
		}
		getLocation().getBlock().getRelative(BlockFace.DOWN, 1).setType(Material.AIR);
		return count;
	}

	public void remove() {
		despawn();
		RetroConomy.getInstance().getManager().ATMS.remove(this);
	}

	public BigDecimal collect() {
		// get all tax from every transaction and clear after getting.
		BigDecimal d = BigDecimal.ZERO;
		for (BankSlip slip : record) {
			d = d.add(slip.getTax());
			Schedule.sync(() -> record.remove(slip)).wait(1);
		}
		return d;
	}

	public void setTax(BigDecimal tax) {
		this.tax = tax;
	}

	public BigDecimal getTax(OfflinePlayer target) {
		if (getOwner().getUniqueId().equals(target.getUniqueId())) {
			return BigDecimal.ZERO;
		}
		return tax;
	}

	public Location getLocation() {
		return location;
	}

	public void save() {
		HUID id = DataContainer.getHuid("Retro-ATM-" + getOwner().getUniqueId().toString());
		if (id != null) {
			DataContainer.deleteInstance(id);
			DataContainer container = new DataContainer("Retro-ATM-" + getOwner().getUniqueId().toString());
			container.setValue(this);
			container.storeTemp();
			container.saveMeta();
		} else {
			DataContainer container = new DataContainer("Retro-ATM-" + getOwner().getUniqueId().toString());
			container.setValue(this);
			container.storeTemp();
			container.saveMeta();
		}
	}

	public boolean use(Block b) {
		if (b.getType() != Material.CHEST)
			return false;
		this.location = b.getLocation().add(0.5, 1, 0.5);
		TileState state = (TileState) b.getState();
		state.getPersistentDataContainer().set(KEY, PersistentDataType.STRING, getOwner().getUniqueId().toString());
		despawn();
		if (state.update(true)) {
			ArmorStand stand = b.getWorld().spawn(location, ArmorStand.class);
			stand.setVisible(false);
			stand.setSmall(true);
			stand.setMarker(true);
			stand.setCustomNameVisible(true);
			stand.setCustomName(StringUtils.use("&6[ATM] &e" + getOwner().getName()).translate());
			save();
			return true;
		}
		return false;
	}

	public static class GUI {

		public static AnvilMenu write(Player p, ATM atm, Type type) {
			switch (type) {
				case DEPOSIT_ACCOUNT:
					return AnvilBuilder.from(p, StringUtils.use("&2Specify a deposit").translate())
							.setLeftItem(builder -> {
								ItemStack paper = new ItemStack(Material.PAPER);
								ItemMeta meta = paper.getItemMeta();
								meta.setDisplayName(StringUtils.use("&aDeposit Amount &2&m→&r ").translate());
								meta.setLore(Collections.singletonList(StringUtils.use("&7Format ##.## &2&m→").translate()));
								paper.setItemMeta(meta);
								builder.setItem(paper);
								builder.setClick((player, text, args) -> {
									if (args.length == 0) {
										try {
											double amount = Double.parseDouble(text.replace(",", "."));
											RetroConomy.getInstance().getManager().getAccount(p).ifPresent(account -> {
												BankSlip slip = account.record(TransactionType.DEPOSIT, p, BigDecimal.valueOf(amount), atm.getTax(p), p.getWorld());
												atm.take(slip);
												Schedule.sync(() -> atm.getLocation().getWorld().dropItemNaturally(atm.getLocation().getBlock().getRelative(BlockFace.UP, 1).getLocation(), slip.get())).wait(1);
											});
											p.closeInventory();
										} catch (NumberFormatException e) {

											return;
										}
									}
									if (args.length > 0) {
										for (String arg : args) {
											try {
												double amount = Double.parseDouble(arg.replace(",", "."));
												RetroConomy.getInstance().getManager().getAccount(p).ifPresent(account -> {
													BankSlip slip = account.record(TransactionType.DEPOSIT, p, BigDecimal.valueOf(amount), atm.getTax(p), p.getWorld());
													atm.take(slip);
													Schedule.sync(() -> atm.getLocation().getWorld().dropItemNaturally(atm.getLocation().getBlock().getRelative(BlockFace.UP, 1).getLocation(), slip.get())).wait(1);
												});
												p.closeInventory();
											} catch (NumberFormatException ignored) {
											}
										}
									}
								});
							})
							.get();
				case WITHDRAW_ACCOUNT:
					return AnvilBuilder.from(p, StringUtils.use("&2Specify a withdrawal").translate())
							.setLeftItem(builder -> {
								ItemStack paper = new ItemStack(Material.PAPER);
								ItemMeta meta = paper.getItemMeta();
								meta.setDisplayName(StringUtils.use("&aWithdraw Amount &2&m→&r ").translate());
								meta.setLore(Collections.singletonList(StringUtils.use("&7Format ##.## &2&m→").translate()));
								paper.setItemMeta(meta);
								builder.setItem(paper);
								builder.setClick((player, text, args) -> {
									if (args.length == 0) {
										try {
											double amount = Double.parseDouble(text.replace(",", "."));
											RetroConomy.getInstance().getManager().getAccount(p).ifPresent(account -> {
												BankSlip slip = account.record(TransactionType.WITHDRAW, p, BigDecimal.valueOf(amount), atm.getTax(p), p.getWorld());
												atm.take(slip);
												Schedule.sync(() -> atm.getLocation().getWorld().dropItemNaturally(atm.getLocation().getBlock().getRelative(BlockFace.UP, 1).getLocation(), slip.get())).wait(1);
											});
											p.closeInventory();
										} catch (NumberFormatException e) {

											return;
										}
									}
									if (args.length > 0) {
										for (String arg : args) {
											try {
												double amount = Double.parseDouble(arg.replace(",", "."));
												RetroConomy.getInstance().getManager().getAccount(p).ifPresent(account -> {
													BankSlip slip = account.record(TransactionType.WITHDRAW, p, BigDecimal.valueOf(amount), atm.getTax(p), p.getWorld());
													atm.take(slip);
													Schedule.sync(() -> atm.getLocation().getWorld().dropItemNaturally(atm.getLocation().getBlock().getRelative(BlockFace.UP, 1).getLocation(), slip.get())).wait(1);
												});
												p.closeInventory();
											} catch (NumberFormatException ignored) {
											}
										}
									}
								});
							})
							.get();
				case DEPOSIT_WALLET:
					return AnvilBuilder.from(p, StringUtils.use("&2Specify a deposit").translate())
							.setLeftItem(builder -> {
								ItemStack paper = new ItemStack(Material.PAPER);
								ItemMeta meta = paper.getItemMeta();
								meta.setDisplayName(StringUtils.use("&aDeposit Amount &2&m→&r ").translate());
								meta.setLore(Collections.singletonList(StringUtils.use("&7Format # &2&m→").translate()));
								paper.setItemMeta(meta);
								builder.setItem(paper);
								builder.setClick((player, text, args) -> {
									if (args.length == 0) {
										try {
											int amount = (int) Double.parseDouble(text.replace(",", "."));
											RetroConomy.getInstance().getManager().getWallet(p).ifPresent(wallet -> Bukkit.dispatchCommand(p, "deposit " + amount));
											p.closeInventory();
										} catch (NumberFormatException e) {

											return;
										}
									}
									if (args.length > 0) {
										for (String arg : args) {
											try {
												int amount = (int) Double.parseDouble(arg.replace(",", "."));
												RetroConomy.getInstance().getManager().getWallet(p).ifPresent(wallet -> Bukkit.dispatchCommand(p, "deposit " + amount));
												p.closeInventory();
											} catch (NumberFormatException ignored) {
											}
										}
									}
								});
							})
							.get();
				case WITHDRAW_WALLET:
					return AnvilBuilder.from(p, StringUtils.use("&2Specify a withdrawal").translate())
							.setLeftItem(builder -> {
								ItemStack paper = new ItemStack(Material.PAPER);
								ItemMeta meta = paper.getItemMeta();
								meta.setDisplayName(StringUtils.use("&aWithdraw Amount &2&m→&r ").translate());
								meta.setLore(Collections.singletonList(StringUtils.use("&7Format # &2&m→").translate()));
								paper.setItemMeta(meta);
								builder.setItem(paper);
								builder.setClick((player, text, args) -> {
									if (args.length == 0) {
										try {
											int amount = (int) Double.parseDouble(text.replace(",", "."));
											RetroConomy.getInstance().getManager().getWallet(p).ifPresent(wallet -> Bukkit.dispatchCommand(p, "withdraw " + amount + " " + ChatColor.stripColor(StringUtils.use(RetroConomy.getInstance().getManager().getCurrencyNames()[0]).translate())));
											p.closeInventory();
										} catch (NumberFormatException e) {

											return;
										}
									}
									if (args.length > 0) {
										for (String arg : args) {
											try {
												int amount = (int) Double.parseDouble(arg.replace(",", "."));
												RetroConomy.getInstance().getManager().getWallet(p).ifPresent(wallet -> Bukkit.dispatchCommand(p, "withdraw " + amount + " " + ChatColor.stripColor(StringUtils.use(RetroConomy.getInstance().getManager().getCurrencyNames()[0]).translate())));
												p.closeInventory();
											} catch (NumberFormatException ignored) {
											}
										}
									}
								});
							})
							.get();
				case FORGOT_CARD:
					return AnvilBuilder.from(p, StringUtils.use("&2Type your account id.").translate())
							.setLeftItem(builder -> {
								ItemStack paper = new ItemStack(Material.PAPER);
								ItemMeta meta = paper.getItemMeta();
								meta.setDisplayName(StringUtils.use("&aAccount ID &2&m→&r ").translate());
								meta.setLore(Collections.singletonList(StringUtils.use("&7Format ####-####-#### &2&m→").translate()));
								paper.setItemMeta(meta);
								builder.setItem(paper);
								builder.setClick((player, text, args) -> {
									try {
										HUID id = HUID.fromString(text);
										if (RetroConomy.getInstance().getManager().getAccount(id).isPresent()) {
											select(atm, Type.ACCOUNT).open(player);
										}
									} catch (Exception ignore) {
									}
								});
							})
							.get();
				default:
					throw new IllegalStateException("Illegal menu type present.");
			}
		}

		public static Menu select(ATM atm, Type type) {
			switch (type) {
				case MAIN:
					return new MenuBuilder(InventoryRows.THREE, "")
							.addElement(new ItemStack(Material.GOLDEN_HELMET))
							.setText(StringUtils.use("&6Wallet").translate())
							.setLore(StringUtils.use("&7Click to deposit/withdraw from your wallet.").translate())
							.setAction(click -> select(atm, Type.WALLET).open(click.getPlayer()))
							.assignToSlots(12)
							.addElement(new ItemStack(Material.ENCHANTED_BOOK))
							.setText(StringUtils.use("&2&m←&r &aChoose a funding source &2&m→").translate())
							.setLore(StringUtils.use("").translate())
							.setAction(click -> {

							})
							.assignToSlots(13)
							.addElement(new ItemStack(Material.IRON_HELMET))
							.setText(StringUtils.use("&eAccount").translate())
							.setLore(StringUtils.use("&7Click to deposit/withdraw bank money.").translate())
							.setAction(click -> select(atm, Type.ACCOUNT_LOGIN).open(click.getPlayer()))
							.assignToSlots(14)
							.setFiller(new ItemStack(Material.GRAY_STAINED_GLASS_PANE))
							.setText(" ")
							.set()
							.create(JavaPlugin.getProvidingPlugin(RetroConomy.class));
				case ACCOUNT_LOGIN:
					return new MenuBuilder(InventoryRows.SIX, "Login (Place your card)")
							.addElement(new ItemStack(Material.AIR))
							.setAction(click -> {
								click.allowClick();
								if (!click.getItemOnMouseCursor().isPresent())
									return;
								if (DebitCard.matches(click.getItemOnMouseCursor().get())) {
									String id = click.getItemOnMouseCursor().get().getItemMeta().getPersistentDataContainer().get(DebitCard.KEY, PersistentDataType.STRING);
									RetroConomy.getInstance().getManager().getAccount(click.getPlayer()).ifPresent(a -> {
										if (id.equals(a.getId().toString())) {
											// access granted
											final ItemStack copy = click.getItemOnMouseCursor().get().clone();
											click.getItemOnMouseCursor().get().setAmount(0);
											click.getPlayer().getWorld().dropItem(click.getPlayer().getLocation(), copy);
											select(atm, Type.ACCOUNT).open(click.getPlayer());
										} else {
											click.getPlayer().closeInventory();
											// access denied
										}
									});
								}
							})
							.assignToSlots(24)
							.addElement(new ItemStack(Material.BLUE_STAINED_GLASS_PANE))
							.setText(StringUtils.use("&7Place your debit card in the slot. &a&m→").translate())
							.assignToSlots(20)
							.addElement(new ItemStack(Material.PLAYER_HEAD))
							.setText(StringUtils.use("&7▼ &3Forgot Card.").translate())
							.setLore()
							.setAction(click -> {
								// open manual id type.
								write(click.getPlayer(), atm, Type.FORGOT_CARD).open();
							})
							.assignToSlots(40)
							.setFiller(new ItemStack(Material.GRAY_STAINED_GLASS_PANE))
							.setText(" ")
							.setAction(MenuClick::disallowClick)
							.set()
							.create(JavaPlugin.getProvidingPlugin(RetroConomy.class));
				case ACCOUNT:
					return new MenuBuilder(InventoryRows.THREE, "Account Options")
							.addElement(new ItemStack(Material.GOLDEN_HELMET))
							.setText(StringUtils.use("&2Withdraw").translate())
							.setLore(StringUtils.use("&7Click to transfer money to your wallet.").translate())
							.setAction(click -> write(click.getPlayer(), atm, Type.WITHDRAW_ACCOUNT).open())
							.assignToSlots(12)
							.addElement(new ItemStack(Material.ENCHANTED_BOOK))
							.setText(StringUtils.use("&2&m←&r &aChoose an option &2&m→").translate())
							.setLore(StringUtils.use("").translate())
							.setAction(click -> {

							})
							.assignToSlots(13)
							.addElement(new ItemStack(Material.IRON_HELMET))
							.setText(StringUtils.use("&aDeposit").translate())
							.setLore(StringUtils.use("&7Click to transfer money to your account.").translate())
							.setAction(click -> write(click.getPlayer(), atm, Type.DEPOSIT_ACCOUNT).open())
							.assignToSlots(14)
							.setFiller(new ItemStack(Material.GRAY_STAINED_GLASS_PANE))
							.setText(" ")
							.set()
							.create(JavaPlugin.getProvidingPlugin(RetroConomy.class));
				case WALLET:
					return new MenuBuilder(InventoryRows.THREE, "Wallet Options")
							.addElement(new ItemStack(Material.GOLDEN_HELMET))
							.setText(StringUtils.use("&2Withdraw").translate())
							.setLore(StringUtils.use("&7Click to withdraw physical money.").translate())
							.setAction(click -> write(click.getPlayer(), atm, Type.WITHDRAW_WALLET).open())
							.assignToSlots(12)
							.addElement(new ItemStack(Material.ENCHANTED_BOOK))
							.setText(StringUtils.use("&2&m←&r &aChoose an option &2&m→").translate())
							.setLore(StringUtils.use("").translate())
							.setAction(click -> {

							})
							.assignToSlots(13)
							.addElement(new ItemStack(Material.IRON_HELMET))
							.setText(StringUtils.use("&aDeposit").translate())
							.setLore(StringUtils.use("&7Click to deposit physical money.").translate())
							.setAction(click -> write(click.getPlayer(), atm, Type.DEPOSIT_WALLET).open())
							.assignToSlots(14)
							.setFiller(new ItemStack(Material.GRAY_STAINED_GLASS_PANE))
							.setText(" ")
							.set()
							.create(JavaPlugin.getProvidingPlugin(RetroConomy.class));
				default:
					throw new IllegalStateException("");
			}
		}

		public enum Type {
			MAIN, WALLET, ACCOUNT, ACCOUNT_LOGIN, FORGOT_CARD, DEPOSIT_ACCOUNT, WITHDRAW_ACCOUNT, DEPOSIT_WALLET, WITHDRAW_WALLET
		}

	}

	public static class Controller implements Listener {

		@EventHandler(priority = EventPriority.LOW)
		public void onBreak(BlockBreakEvent e) {
			Player p = e.getPlayer();
			Block b = e.getBlock();

			ATM atm = pick(b);

			Message msg = Message.form(p).setPrefix(RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.prefix"));

			if (atm != null) {
				if (atm.getOwner().getUniqueId().equals(p.getUniqueId())) {
					msg.send("&aGoodbye.. powering down.");
					atm.remove();
				} else {
					msg.send("&cYou don't own this ATM you can't do this!");
					e.setCancelled(true);
				}
			}
		}

		@EventHandler(priority = EventPriority.LOW)
		public void onInteract(PlayerInteractEvent e) {
			Block b = e.getClickedBlock();
			Player p = e.getPlayer();

			if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {

				ATM atm = pick(b);

				Message msg = Message.form(p).setPrefix(RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.prefix"));

				if (atm != null) {
					e.setCancelled(true);
					if (atm.getOwner().getUniqueId().equals(p.getUniqueId())) {

						GUI.select(atm, GUI.Type.MAIN).open(p);
					} else {
						// open menu
						GUI.select(atm, GUI.Type.MAIN).open(p);
					}
				}
			}

		}

		@EventHandler(priority = EventPriority.LOW)
		public void onPlace(BlockPlaceEvent e) {
			ItemStack i = e.getItemInHand();
			Player p = e.getPlayer();

			if (!(e.getBlock().getState() instanceof TileState))
				return;

			Message msg = Message.form(p).setPrefix(RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.prefix"));
			ATM atm = pick(p);
			if (!has(p)) {
				if (i.isSimilar(atm.get())) {
					Block b = e.getBlock();
					Schedule.sync(() -> {
						if (atm.use(b)) {
							// set the atm location.

							msg.send("&aYou built your own ATM.");
						}
					}).wait(2);
				}
			} else {
				if (!atm.getLocation().getChunk().equals(e.getBlock().getChunk())) {
					atm.remove();
					ATM n = pick(p);
					if (n.use(e.getBlock())) {
						// set the atm location.
						msg.send("&aYou have updated your ATM location.");
					}
				}
			}

		}

	}


}
