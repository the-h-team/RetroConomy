/*
 *  Copyright 2021 Sanctum <https://github.com/the-h-team>
 *
 *  This file is part of RetroConomy.
 *
 *  This software is currently in development and its licensing has not
 *  yet been chosen.
 */
package com.github.sanctum.retro.construct.core;

import com.github.sanctum.labyrinth.Labyrinth;
import com.github.sanctum.labyrinth.data.container.PersistentContainer;
import com.github.sanctum.labyrinth.gui.InventoryRows;
import com.github.sanctum.labyrinth.gui.menuman.Menu;
import com.github.sanctum.labyrinth.gui.menuman.MenuBuilder;
import com.github.sanctum.labyrinth.gui.menuman.MenuClick;
import com.github.sanctum.labyrinth.gui.menuman.PaginatedBuilder;
import com.github.sanctum.labyrinth.gui.menuman.PaginatedClickAction;
import com.github.sanctum.labyrinth.gui.menuman.PaginatedCloseAction;
import com.github.sanctum.labyrinth.gui.printer.AnvilBuilder;
import com.github.sanctum.labyrinth.gui.printer.AnvilMenu;
import com.github.sanctum.labyrinth.library.HUID;
import com.github.sanctum.labyrinth.library.Item;
import com.github.sanctum.labyrinth.library.Message;
import com.github.sanctum.labyrinth.library.NamespacedKey;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.labyrinth.task.Schedule;
import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.util.TransactionType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.TileState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

public class Shop implements Savable {

	public static final Controller CONTROLLER = new Controller();
	private static final long serialVersionUID = -4263717446113447098L;

	private final OfflinePlayer owner;
	protected final List<TransactionStatement> record = new ArrayList<>();
	private Location location = null;
	private final transient HUID id;
	private BigDecimal tax = BigDecimal.valueOf(2.13);

	private boolean locked;

	protected Shop(OfflinePlayer owner) {
		this.owner = owner;
		this.id = HUID.randomID();
		RetroConomy.getInstance().getManager().SHOPS.add(this);
	}

	public static boolean has(OfflinePlayer target) {
		if (RetroConomy.getInstance().getManager().getATMs().list().stream().noneMatch(a -> a.getOwner().getUniqueId().equals(target.getUniqueId()))) {
			return false;
		}
		Shop atm = pick(target);
		return atm.getLocation() != null;
	}

	public static Shop pick(OfflinePlayer target) {
		return !RetroConomy.getInstance().getManager().getATMs().filter(atm -> atm.owner.getUniqueId().equals(target.getUniqueId())).findFirst().isPresent() ? new Shop(target) : RetroConomy.getInstance().getManager().getATMs().filter(atm -> atm.getOwner().getUniqueId().equals(target.getUniqueId())).findFirst().orElse(null);
	}

	public static Shop pick(Block b) {
		org.bukkit.NamespacedKey KEY = new org.bukkit.NamespacedKey(JavaPlugin.getProvidingPlugin(RetroConomy.class), "retro_atm_block");
		if (!(b.getState() instanceof TileState)) {
			return null;
		}
		TileState state = (TileState) b.getState();
		if (!state.getPersistentDataContainer().has(KEY, PersistentDataType.STRING)) {
			return null;
		}
		return RetroConomy.getInstance().getManager().getATMs().filter(a -> {
			if (state.getPersistentDataContainer().get(KEY, PersistentDataType.STRING).equals(a.getOwner().getUniqueId().toString())) {
				if (a.getLocation() == null) {
					a.use(b);
				}
				return true;
			}
			return false;
		}).findFirst().orElse(null);
	}

	public TransactionStatement take(TransactionStatement slip) {
		if (!record.contains(slip)) {
			if (record.size() >= 100) {
				setLocked(true);
				return slip;
			}
			record.add(slip);
		}
		return slip;
	}

	@Override
	public HUID id() {
		BankAccount account = RetroConomy.getInstance().getManager().getAccount(getOwner()).orElse(null);
		return account != null ? account.getId() : id;
	}

	@Override
	public ItemStack toItem() {
		return new Item.Edit(Material.CHEST)
				.setTitle("&6[&3Shop&6] &e" + getOwner().getName())
				.setFlags(ItemFlag.HIDE_ENCHANTS)
				.addEnchantment(Enchantment.VANISHING_CURSE, 1)
				.build();
	}

	public TransactionStatement getTransaction(String id) {
		return record.stream().filter(b -> b.slipId().toString().equals(id)).findFirst().orElse(null);
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
						if (a.getCustomName().equals(StringUtils.use(getOwner().getName() + "'s &2Shop").translate()) || a.getCustomName().equals(StringUtils.use("&eRight click to use me.").translate())) {
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
						if (a.getCustomName().equals(StringUtils.use(getOwner().getName() + "'s &2Shop").translate()) || a.getCustomName().equals(StringUtils.use("&eRight click to use me.").translate())) {
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
		PersistentContainer container = Labyrinth.getContainer(new NamespacedKey(JavaPlugin.getProvidingPlugin(RetroConomy.class), "Shops"));
		container.delete(getOwner().getUniqueId().toString());
		RetroConomy.getInstance().getManager().SHOPS.remove(this);
	}

	public BigDecimal collect() {
		// get all tax from every transaction and clear after getting.
		BigDecimal d = BigDecimal.ZERO;
		for (TransactionStatement slip : record) {
			d = d.add(slip.getTax());
			Schedule.sync(() -> record.remove(slip)).wait(1);
		}
		return d;
	}

	public void setTax(BigDecimal tax) {
		this.tax = tax;
	}

	public BigDecimal getTax(OfflinePlayer target) {
		if (target != null) {
			if (getOwner().getUniqueId().equals(target.getUniqueId())) {
				return BigDecimal.ZERO;
			}
		}
		return tax;
	}

	public Location getLocation() {
		return location;
	}

	public synchronized void save() {
		PersistentContainer container = Labyrinth.getContainer(new NamespacedKey(JavaPlugin.getProvidingPlugin(RetroConomy.class), "Shops"));
		container.attach(getOwner().getUniqueId().toString(), this);
	}

	public synchronized boolean use(Block b) {
		if (b.getType() != Material.CHEST)
			return false;
		this.location = b.getLocation().add(0.5, 1, 0.5);
		TileState state = (TileState) b.getState();
		state.getPersistentDataContainer().set(new org.bukkit.NamespacedKey(JavaPlugin.getProvidingPlugin(RetroConomy.class), "retro_atm_block"), PersistentDataType.STRING, getOwner().getUniqueId().toString());
		int amount = despawn();
		if (getOwner().isOnline()) {
			Message.form(getOwner().getPlayer()).setPrefix(RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.prefix")).send("&8(&7" + amount + "&8) &aold marks were found and removed.");
		}
		if (state.update(true)) {
			ArmorStand stand = b.getWorld().spawn(location, ArmorStand.class);
			stand.setVisible(false);
			stand.setSmall(true);
			stand.setMarker(true);
			stand.setCustomNameVisible(true);
			stand.setCustomName(StringUtils.use(getOwner().getName() + "'s &2Shop").translate());
			Location loc = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ()).subtract(0, 0.2, 0);
			ArmorStand stand2 = b.getWorld().spawn(loc, ArmorStand.class);
			stand2.setVisible(false);
			stand2.setSmall(true);
			stand2.setMarker(true);
			stand2.setCustomNameVisible(true);
			stand2.setCustomName(StringUtils.use("&eRight click to use me.").translate());
			save();
			return true;
		}
		return false;
	}

	public static class GUI {

		private static final Supplier<ItemStack> left = () -> {
			ItemStack s = new ItemStack(Material.DARK_OAK_BUTTON);
			ItemMeta m = s.getItemMeta();
			m.setDisplayName(StringUtils.use("&aPrevious page").translate());
			s.setItemMeta(m);
			return s;
		};
		private static final Supplier<ItemStack> right = () -> {
			ItemStack s = new ItemStack(Material.DARK_OAK_BUTTON);
			ItemMeta m = s.getItemMeta();
			m.setDisplayName(StringUtils.use("&aNext page").translate());
			s.setItemMeta(m);
			return s;
		};
		private static final Supplier<ItemStack> back = () -> {
			ItemStack s = new ItemStack(Material.GREEN_DYE);
			ItemMeta m = s.getItemMeta();
			m.setDisplayName(StringUtils.use("&6Collect the money.").translate());
			s.setItemMeta(m);
			return s;
		};

		public static AnvilMenu write(Shop atm, @Nullable HUID accountId, Type type) {
			switch (type) {
				case DEPOSIT_ACCOUNT:
					return AnvilBuilder.from(StringUtils.use("&2Specify a deposit").translate())
							.setLeftItem(builder -> {
								ItemStack paper = new ItemStack(Material.PAPER);
								ItemMeta meta = paper.getItemMeta();
								meta.setDisplayName(StringUtils.use("&aDeposit Amount &2&m→&r ").translate());
								meta.setLore(Collections.singletonList(StringUtils.use("&7Format ##.## &2&m→").translate()));
								paper.setItemMeta(meta);
								builder.setItem(paper);
								builder.setClick((player, text, args) -> {
									Message msg = Message.form(player).setPrefix(RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.prefix"));
									if (args.length == 0) {
										try {
											double amount = Double.parseDouble(text.replace(",", "."));
											RetroConomy.getInstance().getManager().getAccount(accountId).ifPresent(account -> Schedule.sync(() -> atm.getLocation().getWorld().dropItemNaturally(atm.getLocation().getBlock().getRelative(BlockFace.UP, 1).getLocation(), account.record(atm, TransactionType.DEPOSIT, player, BigDecimal.valueOf(amount)).toItem())).wait(1));
											msg.send("&a&oPrinting your receipt...");
											player.closeInventory();

										} catch (NumberFormatException e) {

											return;
										}
									}
									if (args.length > 0) {
										for (String arg : args) {
											try {
												double amount = Double.parseDouble(arg.replace(",", "."));
												RetroConomy.getInstance().getManager().getAccount(accountId).ifPresent(account -> Schedule.sync(() -> atm.getLocation().getWorld().dropItemNaturally(atm.getLocation().getBlock().getRelative(BlockFace.UP, 1).getLocation(), account.record(atm, TransactionType.DEPOSIT, player, BigDecimal.valueOf(amount)).toItem())).wait(1));
												msg.send("&a&oPrinting your receipt...");
												player.closeInventory();
											} catch (NumberFormatException ignored) {
											}
										}
									}
								});
							})
							.get().applyClosingLogic((player, view, menu) -> HandlerList.unregisterAll(menu.getListener()));
				case WITHDRAW_ACCOUNT:
					return AnvilBuilder.from(StringUtils.use("&2Specify a withdrawal").translate())
							.setLeftItem(builder -> {
								ItemStack paper = new ItemStack(Material.PAPER);
								ItemMeta meta = paper.getItemMeta();
								meta.setDisplayName(StringUtils.use("&aWithdraw Amount &2&m→&r ").translate());
								meta.setLore(Collections.singletonList(StringUtils.use("&7Format ##.## &2&m→").translate()));
								paper.setItemMeta(meta);
								builder.setItem(paper);
								builder.setClick((player, text, args) -> {
									Message msg = Message.form(player).setPrefix(RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.prefix"));
									if (args.length == 0) {
										try {
											double amount = Double.parseDouble(text.replace(",", "."));
											RetroConomy.getInstance().getManager().getAccount(accountId).ifPresent(account -> Schedule.sync(() -> atm.getLocation().getWorld().dropItemNaturally(atm.getLocation().getBlock().getRelative(BlockFace.UP, 1).getLocation(), account.record(atm, TransactionType.WITHDRAW, player, BigDecimal.valueOf(amount)).toItem())).wait(1));
											msg.send("&a&oPrinting your receipt...");
											player.closeInventory();
										} catch (NumberFormatException e) {

											return;
										}
									}
									if (args.length > 0) {
										for (String arg : args) {
											try {
												double amount = Double.parseDouble(arg.replace(",", "."));
												RetroConomy.getInstance().getManager().getAccount(accountId).ifPresent(account -> Schedule.sync(() -> atm.getLocation().getWorld().dropItemNaturally(atm.getLocation().getBlock().getRelative(BlockFace.UP, 1).getLocation(), account.record(atm, TransactionType.WITHDRAW, player, BigDecimal.valueOf(amount)).toItem())).wait(1));
												msg.send("&a&oPrinting your receipt...");
												player.closeInventory();
											} catch (NumberFormatException ignored) {
											}
										}
									}
								});
							})
							.get().applyClosingLogic((player, view, menu) -> HandlerList.unregisterAll(menu.getListener()));
				case DEPOSIT_WALLET:
					return AnvilBuilder.from(StringUtils.use("&2Specify a deposit").translate())
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
											RetroConomy.getInstance().getManager().getWallet(player).ifPresent(wallet -> Bukkit.dispatchCommand(player, "deposit " + amount));
											player.closeInventory();
										} catch (NumberFormatException e) {

											return;
										}
									}
									if (args.length > 0) {
										for (String arg : args) {
											try {
												int amount = (int) Double.parseDouble(arg.replace(",", "."));
												RetroConomy.getInstance().getManager().getWallet(player).ifPresent(wallet -> Bukkit.dispatchCommand(player, "deposit " + amount));
												player.closeInventory();
											} catch (NumberFormatException ignored) {
											}
										}
									}
								});
							})
							.get().applyClosingLogic((player, view, menu) -> HandlerList.unregisterAll(menu.getListener()));
				case WITHDRAW_WALLET:
					return AnvilBuilder.from(StringUtils.use("&2Specify a withdrawal").translate())
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
											RetroConomy.getInstance().getManager().getWallet(player).ifPresent(wallet -> Bukkit.dispatchCommand(player, "withdraw " + amount + " " + ChatColor.stripColor(StringUtils.use(RetroConomy.getInstance().getManager().getCurrencyNames()[0]).translate())));
											player.closeInventory();
										} catch (NumberFormatException e) {

											return;
										}
									}
									if (args.length > 0) {
										for (String arg : args) {
											try {
												int amount = (int) Double.parseDouble(arg.replace(",", "."));
												RetroConomy.getInstance().getManager().getWallet(player).ifPresent(wallet -> Bukkit.dispatchCommand(player, "withdraw " + amount + " " + ChatColor.stripColor(StringUtils.use(RetroConomy.getInstance().getManager().getCurrencyNames()[0]).translate())));
												player.closeInventory();
											} catch (NumberFormatException ignored) {
											}
										}
									}
								});
							})
							.get().applyClosingLogic((player, view, menu) -> HandlerList.unregisterAll(menu.getListener()));
				case TAX_EDIT:
					return AnvilBuilder.from(StringUtils.use("&6Type the new tax amount.").translate())
							.setLeftItem(builder -> {
								ItemStack paper = new ItemStack(Material.PAPER);
								ItemMeta meta = paper.getItemMeta();
								meta.setDisplayName(StringUtils.use("&eTax ##.## &6&m→&r ").translate());
								meta.setLore(Collections.singletonList(StringUtils.use("&7Format ##.## &6&m→").translate()));
								paper.setItemMeta(meta);
								builder.setItem(paper);
								builder.setClick((player, text, args) -> {
									if (args.length == 0) {
										try {
											double amount = Double.parseDouble(text.replace(",", "."));
											atm.setTax(BigDecimal.valueOf(amount));
											Message.form(player).setPrefix(RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.prefix")).send("&aTax adjusted to &r" + amount);
											player.closeInventory();
										} catch (NumberFormatException e) {

											return;
										}
									}
									if (args.length > 0) {
										for (String arg : args) {
											try {
												double amount = Double.parseDouble(arg.replace(",", "."));
												atm.setTax(BigDecimal.valueOf(amount));
												Message.form(player).setPrefix(RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.prefix")).send("&aTax adjusted to &r" + amount);
												player.closeInventory();
											} catch (NumberFormatException ignored) {
											}
										}
									}
								});
							})
							.get().applyClosingLogic((player, view, menu) -> HandlerList.unregisterAll(menu.getListener()));
				case FORGOT_CARD:
					return AnvilBuilder.from(StringUtils.use("&2Type your account id.").translate())
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
											select(atm, id, Type.ACCOUNT).open(player);
										}
									} catch (Exception ignore) {
									}
								});
							})
							.get().applyClosingLogic((player, view, menu) -> HandlerList.unregisterAll(menu.getListener()));
				default:
					throw new IllegalStateException("Illegal menu type present.");
			}
		}

		public static Menu.Paginated<TransactionStatement> browse(Shop atm) {
			return new PaginatedBuilder<>(atm.record)
					.forPlugin(JavaPlugin.getProvidingPlugin(RetroConomy.class))
					.setTitle(StringUtils.use("").translate())
					.setSize(InventoryRows.SIX)
					.setCloseAction(PaginatedCloseAction::clear)
					.setAlreadyFirst(StringUtils.use("").translate())
					.setAlreadyLast(StringUtils.use("").translate())
					.setNavigationBack(back.get(), 49, click -> {
						RetroAccount wallet = RetroConomy.getInstance().getManager().getWallet(click.getPlayer()).orElse(null);
						if (wallet != null) {
							final BigDecimal amount = atm.collect();
							if (wallet.deposit(amount, click.getPlayer().getWorld()).success()) {
								Message.form(click.getPlayer()).setPrefix(RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.prefix")).send("&aYou received " + RetroConomy.getInstance().getManager().format(amount) + " of tax.");
							}
						}
						Shop.GUI.browse(atm).open(click.getPlayer());
					})
					.setNavigationLeft(left.get(), 48, PaginatedClickAction::sync)
					.setNavigationRight(right.get(), 50, PaginatedClickAction::sync)
					.setupProcess(process -> {
						process.setItem(() -> process.getContext().toItem()).setClick(click -> {
							TransactionStatement slip = process.getContext();
							RetroAccount wallet = RetroConomy.getInstance().getManager().getWallet(click.getPlayer()).orElse(null);
							if (wallet != null) {
								final BigDecimal amount = slip.getTax();
								if (wallet.deposit(amount, click.getPlayer().getWorld()).success()) {
									Message.form(click.getPlayer()).setPrefix(RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.prefix")).send("&aYou received " + RetroConomy.getInstance().getManager().format(amount) + " of tax.");
								}
								atm.record.remove(slip);
								Shop.GUI.browse(atm).open(click.getPlayer());
							}
						});
					})
					.setupBorder()
					.setFillType(Material.GREEN_STAINED_GLASS_PANE)
					.setBorderType(Material.GRAY_STAINED_GLASS_PANE)
					.build()
					.extraElements()
					.invoke(() -> {
						ItemStack i = new ItemStack(Material.TOTEM_OF_UNDYING);
						ItemMeta meta = i.getItemMeta();
						meta.setDisplayName(StringUtils.use("&8(&fMain Menu&8)").translate());
						i.setItemMeta(meta);
						return i;
					}, 45, click -> select(atm, null, Type.ADMIN_PANEL).open(click.getPlayer()))
					.add()
					.limit(28)
					.build();
		}

		public static Menu select(Shop atm, HUID account, Type type) {
			switch (type) {
				case ADMIN_PANEL:
					return new MenuBuilder(InventoryRows.THREE, "")
							.addElement(new ItemStack(Material.PAPER))
							.setText(StringUtils.use("&eLog").translate())
							.setLore(StringUtils.use("&7View the list of transactions to collect.").translate())
							.setAction(click -> browse(atm).open(click.getPlayer()))
							.assignToSlots(10)
							.addElement(new ItemStack(Material.GOLDEN_HELMET))
							.setText(StringUtils.use("&6Wallet").translate())
							.setLore(StringUtils.use("&7Click to deposit/withdraw from your wallet.").translate())
							.setAction(click -> select(atm, null, Type.WALLET).open(click.getPlayer()))
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
							.setAction(click -> select(atm, null, Type.ACCOUNT_LOGIN).open(click.getPlayer()))
							.assignToSlots(14)
							.addElement(new ItemStack(Material.NAME_TAG))
							.setText(StringUtils.use("&6Tax").translate())
							.setLore(StringUtils.use("&7Adjust the transaction tax for account usage.").translate())
							.setAction(click -> write(atm, null, Type.TAX_EDIT).setViewer(click.getPlayer()).open())
							.assignToSlots(16)
							.addElement(new Item.Edit(Material.BOOKSHELF).setTitle("&3[&bShop&3]").setLore("&7Click to browse.").build())
							.setAction(click -> ItemDemand.GUI.playerSelect(atm.getOwner().getUniqueId()).open(click.getPlayer()))
							.assignToSlots(22)
							.setFiller(new ItemStack(Material.GRAY_STAINED_GLASS_PANE))
							.setText(" ")
							.set()
							.create(JavaPlugin.getProvidingPlugin(RetroConomy.class));
				case MAIN:
					return new MenuBuilder(InventoryRows.THREE, "")
							.addElement(new ItemStack(Material.GOLDEN_HELMET))
							.setText(StringUtils.use("&6Wallet").translate())
							.setLore(StringUtils.use("&7Click to deposit/withdraw from your wallet.").translate())
							.setAction(click -> select(atm, null, Type.WALLET).open(click.getPlayer()))
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
							.setAction(click -> select(atm, null, Type.ACCOUNT_LOGIN).open(click.getPlayer()))
							.assignToSlots(14)
							.addElement(new Item.Edit(Material.BOOKSHELF).setTitle("&3[&bShop&3]").setLore("&7Click to browse.").build())
							.setAction(click -> ItemDemand.GUI.playerSelect(atm.getOwner().getUniqueId()).open(click.getPlayer()))
							.assignToSlots(22)
							.setFiller(new ItemStack(Material.GRAY_STAINED_GLASS_PANE))
							.setText(" ")
							.set()
							.create(JavaPlugin.getProvidingPlugin(RetroConomy.class));
				case ACCOUNT_LOGIN:
					return new MenuBuilder(InventoryRows.SIX, "Login (Place your card)")
							.addElement(new ItemStack(Material.AIR))
							.setAction(click -> {
								click.allowClick();
								Message msg = Message.form(click.getPlayer()).setPrefix(RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.prefix"));
								if (!click.getItemOnMouseCursor().isPresent())
									return;
								if (DebitCard.matches(click.getItemOnMouseCursor().get())) {
									String id = click.getItemOnMouseCursor().get().getItemMeta().getPersistentDataContainer().get(DebitCard.KEY, PersistentDataType.STRING);

									RetroConomy.getInstance().getManager().getAccount(HUID.fromString(id)).ifPresent(a -> {
										if (a.getOwner().equals(click.getPlayer().getUniqueId()) || a.getJointOwner() != null && a.getJointOwner().equals(click.getPlayer().getUniqueId()) || a.getMembers().contains(click.getPlayer().getUniqueId())) {
											// access granted
											final ItemStack copy = click.getItemOnMouseCursor().get().clone();
											click.getItemOnMouseCursor().get().setAmount(0);
											click.getPlayer().getWorld().dropItem(click.getPlayer().getLocation(), copy);
											select(atm, a.getId(), Type.ACCOUNT).open(click.getPlayer());
										} else {
											click.getPlayer().closeInventory();
											// access denied
											Schedule.sync(() -> click.getPlayer().getWorld().playSound(atm.getLocation(), Sound.ENTITY_VILLAGER_NO, 10, 10)).cancelAfter(18).repeat(0, 20);
											msg.send("&cYou don't have access to this bank account.");
										}
									});
								} else {

									msg.send("&cThis debit card is invalid.");
									click.getPlayer().getWorld().playSound(click.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 10, 1);
									click.getPlayer().closeInventory();
								}
							})
							.assignToSlots(24)
							.addElement(new ItemStack(Material.BLUE_STAINED_GLASS_PANE))
							.setText(StringUtils.use("&7Place your debit card in the slot. &a&m→").translate())
							.assignToSlots(20)
							.addElement(new ItemStack(Material.GOLD_NUGGET))
							.setText(StringUtils.use("&8(&7Tax&8) &a&m→&6 " + RetroConomy.getInstance().getManager().format(atm.getTax(null))).translate())
							.assignToSlots(22)
							.addElement(new ItemStack(Material.PLAYER_HEAD))
							.setText(StringUtils.use("&7▼ &3Forgot Card.").translate())
							.setLore()
							.setAction(click -> {
								// open manual id type.
								write(atm, null, Type.FORGOT_CARD).setViewer(click.getPlayer()).open();
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
							.setAction(click -> write(atm, account, Type.WITHDRAW_ACCOUNT).setViewer(click.getPlayer()).open())
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
							.setAction(click -> write(atm, account, Type.DEPOSIT_ACCOUNT).setViewer(click.getPlayer()).open())
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
							.setAction(click -> write(atm, null, Type.WITHDRAW_WALLET).setViewer(click.getPlayer()).open())
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
							.setAction(click -> write(atm, null, Type.DEPOSIT_WALLET).setViewer(click.getPlayer()).open())
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
			MAIN, ADMIN_PANEL, LOG, WALLET, ACCOUNT, ACCOUNT_LOGIN, FORGOT_CARD, TAX_EDIT, DEPOSIT_ACCOUNT, WITHDRAW_ACCOUNT, DEPOSIT_WALLET, WITHDRAW_WALLET
		}

	}

	public static class Controller implements Listener {

		@EventHandler(priority = EventPriority.LOW)
		public void onBreak(BlockBreakEvent e) {
			Player p = e.getPlayer();
			Block b = e.getBlock();
			if (e.isCancelled())
				return;

			Shop atm = pick(b);

			Message msg = Message.form(p).setPrefix(RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.prefix"));

			if (atm != null) {
				if (atm.getOwner().getUniqueId().equals(p.getUniqueId())) {
					msg.send("&aGoodbye.. powering down.");
					e.setDropItems(false);
					p.getWorld().dropItem(b.getLocation(), atm.toItem());
					atm.remove();
				} else {
					msg.send("&cYou don't own this shop you can't do this!");
					p.getWorld().playSound(p.getLocation(), Sound.ENTITY_VILLAGER_AMBIENT, 10, 1);
					e.setCancelled(true);
				}
			}
		}

		@EventHandler(priority = EventPriority.LOW)
		public void onInteract(PlayerInteractEvent e) {
			Player p = e.getPlayer();

			if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				Block b = e.getClickedBlock();

				if (b == null)
					return;

				Shop atm = pick(b);

				if (atm != null) {
					e.setCancelled(true);
					if (atm.getOwner().getUniqueId().equals(p.getUniqueId())) {

						GUI.select(atm, null, GUI.Type.ADMIN_PANEL).open(p);
					} else {
						GUI.select(atm, null, GUI.Type.MAIN).open(p);
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

			if (i.hasItemMeta() && StringUtils.use(i.getItemMeta().getDisplayName()).containsIgnoreCase("Shop")) {

				Message msg = Message.form(p).setPrefix(RetroConomy.getInstance().getManager().getMain().getConfig().getString("Options.prefix"));
				Shop atm = pick(p);
				if (!has(p)) {
					if (i.isSimilar(atm.toItem())) {
						Block b = e.getBlock();
						Schedule.sync(() -> {
							if (atm.use(b)) {
								// set the atm location.

								msg.send("&aYou built a market access point.");
								e.getItemInHand().setAmount(0);
							}
						}).wait(2);
					}
				} else {
					if (!atm.getLocation().getChunk().equals(e.getBlock().getChunk())) {

						atm.remove();
						Shop n = pick(p);
						if (n.use(e.getBlock())) {
							// set the atm location.
							e.getItemInHand().setAmount(0);
							msg.send("&aYou have updated your market access point location.");
						}
					}
				}

			}
		}

	}


}
