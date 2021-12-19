/*
 *  Copyright 2021 Sanctum <https://github.com/the-h-team>
 *
 *  This file is part of RetroConomy.
 *
 *  This software is currently in development and its licensing has not
 *  yet been chosen.
 */
package com.github.sanctum.retro.construct.core;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.data.container.PersistentContainer;
import com.github.sanctum.labyrinth.gui.unity.construct.Menu;
import com.github.sanctum.labyrinth.gui.unity.construct.PaginatedMenu;
import com.github.sanctum.labyrinth.gui.unity.construct.SingularMenu;
import com.github.sanctum.labyrinth.gui.unity.impl.BorderElement;
import com.github.sanctum.labyrinth.gui.unity.impl.FillerElement;
import com.github.sanctum.labyrinth.gui.unity.impl.ItemElement;
import com.github.sanctum.labyrinth.gui.unity.impl.ListElement;
import com.github.sanctum.labyrinth.gui.unity.impl.MenuType;
import com.github.sanctum.labyrinth.library.HUID;
import com.github.sanctum.labyrinth.library.Item;
import com.github.sanctum.labyrinth.library.Message;
import com.github.sanctum.labyrinth.library.NamespacedKey;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.labyrinth.task.Schedule;
import com.github.sanctum.retro.RetroConomy;
import com.github.sanctum.retro.api.ItemDemand;
import com.github.sanctum.retro.api.RetroAccount;
import com.github.sanctum.retro.api.Savable;
import com.github.sanctum.retro.util.TransactionType;
import com.github.sanctum.skulls.SkullType;
import java.math.BigDecimal;
import java.util.ArrayList;
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
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
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
		RetroConomy.getInstance().getManager().getShops().add(this);
	}

	public static boolean has(OfflinePlayer target) {
		if (RetroConomy.getInstance().getManager().getShops().stream().noneMatch(a -> a.getOwner().getUniqueId().equals(target.getUniqueId()))) {
			return false;
		}
		Shop atm = pick(target);
		return atm.getLocation() != null;
	}

	public static Shop pick(OfflinePlayer target) {
		return RetroConomy.getInstance().getManager().getShops().stream().noneMatch(atm -> atm.owner.getUniqueId().equals(target.getUniqueId())) ? new Shop(target) : RetroConomy.getInstance().getManager().getShops().stream().filter(atm -> atm.getOwner().getUniqueId().equals(target.getUniqueId())).findFirst().orElse(null);
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
		return RetroConomy.getInstance().getManager().getShops().stream().filter(a -> {
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
		PersistentContainer container = LabyrinthProvider.getInstance().getContainer(new NamespacedKey(JavaPlugin.getProvidingPlugin(RetroConomy.class), "Shops"));
		container.delete(getOwner().getUniqueId().toString());
		RetroConomy.getInstance().getManager().getShops().remove(this);
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
		PersistentContainer container = LabyrinthProvider.getInstance().getContainer(new NamespacedKey(JavaPlugin.getProvidingPlugin(RetroConomy.class), "Shops"));
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
			Message.form(getOwner().getPlayer()).setPrefix(RetroConomy.getInstance().getManager().getMain().getRoot().getString("Options.prefix")).send("&8(&7" + amount + "&8) &aold marks were found and removed.");
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

		private static final Plugin plugin = JavaPlugin.getProvidingPlugin(RetroConomy.class);

		private static final Supplier<ItemStack> left = () -> {
			ItemStack s = new ItemStack(SkullType.ARROW_BLACK_LEFT.get());
			ItemMeta m = s.getItemMeta();
			m.setDisplayName(StringUtils.use("&aPrevious page").translate());
			s.setItemMeta(m);
			return s;
		};
		private static final Supplier<ItemStack> right = () -> {
			ItemStack s = new ItemStack(SkullType.ARROW_BLACK_RIGHT.get());
			ItemMeta m = s.getItemMeta();
			m.setDisplayName(StringUtils.use("&aNext page").translate());
			s.setItemMeta(m);
			return s;
		};
		private static final Supplier<ItemStack> back = () -> {
			ItemStack s = new ItemStack(SkullType.ARROW_BLACK_DOWN.get());
			ItemMeta m = s.getItemMeta();
			m.setDisplayName(StringUtils.use("&6Collect the money.").translate());
			s.setItemMeta(m);
			return s;
		};

		public static Menu write(Shop atm, @Nullable HUID accountId, Type type) {
			switch (type) {
				case DEPOSIT_ACCOUNT:
					return MenuType.PRINTABLE.build()
							.setHost(plugin)
							.setTitle("&2Specify a deposit")
							.setSize(Menu.Rows.ONE)
							.setStock(i -> {

								i.addItem(it -> {
									it.setElement(ed -> ed.setType(Material.PAPER).setTitle("&aDeposit Amount &2&m→&r ").setLore("&7Format ##.## &2&m→").build());
									it.setType(ItemElement.ControlType.DISPLAY);
									it.setSlot(0);
								});

							})
							.join().addAction(click -> {
								if (click.getSlot() == 2) {
									click.setCancelled(true);
									String[] args = click.getParent().getName().split(" ");
									Player player = click.getElement();
									Message msg = Message.form(player).setPrefix(RetroConomy.getInstance().getManager().getMain().getRoot().getString("Options.prefix"));
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
				case WITHDRAW_ACCOUNT:
					return MenuType.PRINTABLE.build()
							.setHost(plugin)
							.setTitle("&2Specify a withdrawal")
							.setSize(Menu.Rows.ONE)
							.setStock(i -> {

								i.addItem(it -> {
									it.setElement(ed -> ed.setType(Material.PAPER).setTitle("&aWithdraw Amount &2&m→&r ").setLore("&7Format ##.## &2&m→").build());
									it.setType(ItemElement.ControlType.DISPLAY);
									it.setSlot(0);
								});

							})
							.join().addAction(click -> {
								if (click.getSlot() == 2) {
									click.setCancelled(true);
									String[] args = click.getParent().getName().split(" ");
									Player player = click.getElement();
									Message msg = Message.form(player).setPrefix(RetroConomy.getInstance().getManager().getMain().getRoot().getString("Options.prefix"));
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
				case DEPOSIT_WALLET:
					return MenuType.PRINTABLE.build()
							.setHost(plugin)
							.setTitle("&2Specify a deposit")
							.setSize(Menu.Rows.ONE)
							.setStock(i -> {

								i.addItem(it -> {
									it.setElement(ed -> ed.setType(Material.PAPER).setTitle("&aDeposit Amount &2&m→&r ").setLore("&7Format ##.## &2&m→").build());
									it.setType(ItemElement.ControlType.DISPLAY);
									it.setSlot(0);
								});

							})
							.join().addAction(click -> {
								if (click.getSlot() == 2) {
									click.setCancelled(true);
									String[] args = click.getParent().getName().split(" ");
									Player player = click.getElement();
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
				case WITHDRAW_WALLET:
					return MenuType.PRINTABLE.build()
							.setHost(plugin)
							.setTitle("&2Specify a withdrawal")
							.setSize(Menu.Rows.ONE)
							.setStock(i -> {

								i.addItem(it -> {
									it.setElement(ed -> ed.setType(Material.PAPER).setTitle("&aWithdraw Amount &2&m→&r ").setLore("&7Format ##.## &2&m→").build());
									it.setType(ItemElement.ControlType.DISPLAY);
									it.setSlot(0);
								});

							})
							.join().addAction(click -> {
								if (click.getSlot() == 2) {
									click.setCancelled(true);
									String[] args = click.getParent().getName().split(" ");
									Player player = click.getElement();
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
				case TAX_EDIT:
					return MenuType.PRINTABLE.build()
							.setHost(plugin)
							.setTitle("&6Type the new tax amount.")
							.setSize(Menu.Rows.ONE)
							.setStock(i -> {
								i.addItem(it -> {
									it.setElement(ed -> ed.setType(Material.PAPER).setTitle("&eTax ##.## &6&m→&r ").setLore("&7Format ##.## &2&m→").build());
									it.setType(ItemElement.ControlType.DISPLAY);
									it.setSlot(0);
								});

							})
							.join().addAction(click -> {
								if (click.getSlot() == 2) {
									click.setCancelled(true);
									String[] args = click.getParent().getName().split(" ");
									Player player = click.getElement();
									for (String arg : args) {
										try {
											double amount = Double.parseDouble(arg.replace(",", "."));
											atm.setTax(BigDecimal.valueOf(amount));
											Message.form(player).setPrefix(RetroConomy.getInstance().getManager().getMain().getRoot().getString("Options.prefix")).send("&aTax adjusted to &r" + amount);
											player.closeInventory();
										} catch (NumberFormatException ignored) {
										}
									}
								}
							});
				case FORGOT_CARD:
					return MenuType.PRINTABLE.build()
							.setHost(plugin)
							.setTitle("&2Type your account id.")
							.setSize(Menu.Rows.ONE)
							.setStock(i -> {
								i.addItem(it -> {
									it.setElement(ed -> ed.setType(Material.PAPER).setTitle("&aAccount ID &2&m→&r ").setLore("&7Format ####-####-#### &2&m→").build());
									it.setType(ItemElement.ControlType.DISPLAY);
									it.setSlot(0);
								});

							})
							.join().addAction(click -> {
								if (click.getSlot() == 2) {
									click.setCancelled(true);
									Player player = click.getElement();
									HUID id = HUID.fromString(click.getParent().getName());
									if (id != null) {
										if (RetroConomy.getInstance().getManager().getAccount(id).isPresent()) {
											select(atm, id, Type.ACCOUNT).open(player);
										}
									}
								}
							});
				default:
					throw new IllegalStateException("Illegal menu type present.");
			}
		}

		public static Menu viewShop(Shop atm) {
			return MenuType.PAGINATED.build()
					.setHost(plugin)
					.setSize(Menu.Rows.SIX)
					.setKey("shop-" + atm.id().toString())
					.setProperty(Menu.Property.CACHEABLE)
					.setTitle("Transaction log.")
					.setStock(i -> {
						ListElement<TransactionStatement> list = new ListElement<>(atm.record);
						list.setLimit(28);
						list.setPopulate((statement, item) -> {

							item.setElement(ed -> ed.setItem(statement.toItem()).build());
							item.setClick(click -> {
								RetroAccount wallet = RetroConomy.getInstance().getManager().getWallet(click.getElement()).orElse(null);
								if (wallet != null) {
									final BigDecimal amount = statement.getTax();
									if (wallet.deposit(amount, click.getElement().getWorld()).success()) {
										Message.form(click.getElement()).setPrefix(RetroConomy.getInstance().getManager().getMain().getRoot().getString("Options.prefix")).send("&aYou received " + RetroConomy.getInstance().getManager().format(amount) + " of tax.");
									}
									atm.record.remove(statement);
									Shop.GUI.viewShop(atm).open(click.getElement());
								}
							});

						});
						BorderElement<?> border = new BorderElement<>(i);
						FillerElement<?> filler = new FillerElement<>(i);

						for (Menu.Panel p : Menu.Panel.values()) {
							if (p != Menu.Panel.MIDDLE) {
								border.add(p, ed -> {
									ed.setElement(it -> it.setType(Material.IRON_BARS).setTitle(" ").build());
								});
							}
						}
						filler.add(ed -> {
							ed.setElement(it -> it.setType(Material.GRAY_STAINED_GLASS_PANE).setTitle(" ").build());
						});
						i.addItem(filler);
						i.addItem(border);
						i.addItem(it -> {
							it.setElement(back.get());
							it.setSlot(49);
							it.setClick(click -> {
								click.setCancelled(true);
								RetroAccount wallet = RetroConomy.getInstance().getManager().getWallet(click.getElement()).orElse(null);
								if (wallet != null) {
									final BigDecimal amount = atm.collect();
									if (wallet.deposit(amount, click.getElement().getWorld()).success()) {
										Message.form(click.getElement()).setPrefix(RetroConomy.getInstance().getManager().getMain().getRoot().getString("Options.prefix")).send("&aYou received " + RetroConomy.getInstance().getManager().format(amount) + " of tax.");
									}
								}
								Shop.GUI.viewShop(atm).open(click.getElement());
							});
						});
						i.addItem(it -> {
							it.setElement(right.get());
							it.setSlot(50);
							it.setType(ItemElement.ControlType.BUTTON_NEXT);
						});
						i.addItem(it -> {
							it.setElement(left.get());
							it.setSlot(48);
							it.setType(ItemElement.ControlType.BUTTON_BACK);
						});
						i.addItem(it -> {
							it.setElement(ed -> ed.setType(Material.TOTEM_OF_UNDYING).setTitle("&8(&fMain Menu&8)").build());
							it.setSlot(45);
							it.setClick(click -> {
								click.setCancelled(true);
								select(atm, null, Type.ADMIN_PANEL).open(click.getElement());
							});
						});

					})
					.orGet(m -> m instanceof PaginatedMenu && m.getKey().map(("shop-" + atm.id().toString())::equals).orElse(false));
		}

		public static Menu select(Shop atm, HUID account, Type type) {
			switch (type) {
				case ADMIN_PANEL:
					return MenuType.SINGULAR.build()
							.setTitle("Admin access")
							.setKey("admin-panel-" + atm.id().toString())
							.setHost(plugin)
							.setSize(Menu.Rows.THREE)
							.setProperty(Menu.Property.CACHEABLE)
							.setStock(i -> {
								i.addItem(ed -> {
									ed.setElement(it -> it.setType(Material.PAPER).setTitle("&eLog").setLore("&7View the list of transactions to collect.").build());
									ed.setSlot(10);
									ed.setClick(click -> {
										click.setCancelled(true);
										viewShop(atm).open(click.getElement());
									});
								});
								i.addItem(ed -> {
									ed.setElement(it -> it.setType(Material.GOLDEN_HELMET).setTitle("&6Wallet").setLore("&7Click to deposit/withdraw from your wallet.").build());
									ed.setSlot(12);
									ed.setClick(click -> {
										click.setCancelled(true);
										select(atm, null, Type.WALLET).open(click.getElement());
									});
								});
								i.addItem(ed -> {
									ed.setElement(it -> it.setType(Material.ENCHANTED_BOOK).setTitle(" ").setLore("&2&m←&r &aChoose a funding source &2&m→").build());
									ed.setSlot(13);
									ed.setType(ItemElement.ControlType.DISPLAY);
								});
								i.addItem(ed -> {
									ed.setElement(it -> it.setType(Material.IRON_HELMET).setTitle("&eAccount").setLore("&7Click to deposit/withdraw bank money.").build());
									ed.setSlot(14);
									ed.setClick(click -> {
										click.setCancelled(true);
										select(atm, null, Type.ACCOUNT_LOGIN).open(click.getElement());
									});
								});
								i.addItem(ed -> {
									ed.setElement(it -> it.setType(Material.NAME_TAG).setTitle("&6Tax").setLore("&7Adjust the transaction tax for account usage.").build());
									ed.setSlot(16);
									ed.setClick(click -> {
										click.setCancelled(true);
										write(atm, null, Type.TAX_EDIT).open(click.getElement());
									});
								});
								i.addItem(ed -> {
									ed.setElement(it -> it.setType(Material.BOOKSHELF).setTitle("&3[&bShop&3]").setLore("&7Click to browse.").build());
									ed.setSlot(22);
									ed.setClick(click -> {
										click.setCancelled(true);
										ItemDemand.GUI.selectShopCategory(atm.getOwner().getUniqueId()).open(click.getElement());
									});
								});
								FillerElement<?> filler = new FillerElement<>(i);
								filler.add(ed -> {
									ed.setElement(it -> it.setType(Material.GRAY_STAINED_GLASS_PANE).setTitle(" ").build());
								});
							})
							.orGet(m -> m instanceof SingularMenu && m.getKey().map(("admin-panel-" + atm.id().toString())::equals).orElse(false));
				case MAIN:
					return MenuType.SINGULAR.build()
							.setTitle(" ")
							.setKey("main-panel-" + atm.id.toString())
							.setHost(plugin)
							.setSize(Menu.Rows.THREE)
							.setProperty(Menu.Property.CACHEABLE)
							.setStock(i -> {
								i.addItem(ed -> {
									ed.setElement(it -> it.setType(Material.GOLDEN_HELMET).setTitle("&6Wallet").setLore("&7Click to deposit/withdraw from your wallet.").build());
									ed.setSlot(12);
									ed.setClick(click -> {
										click.setCancelled(true);
										select(atm, null, Type.WALLET).open(click.getElement());
									});
								});
								i.addItem(ed -> {
									ed.setElement(it -> it.setType(Material.ENCHANTED_BOOK).setTitle(" ").setLore("&2&m←&r &aChoose a funding source &2&m→").build());
									ed.setSlot(13);
									ed.setType(ItemElement.ControlType.DISPLAY);
								});
								i.addItem(ed -> {
									ed.setElement(it -> it.setType(Material.IRON_HELMET).setTitle("&eAccount").setLore("&7Click to deposit/withdraw bank money.").build());
									ed.setSlot(14);
									ed.setClick(click -> {
										click.setCancelled(true);
										select(atm, null, Type.ACCOUNT_LOGIN).open(click.getElement());
									});
								});
								i.addItem(ed -> {
									ed.setElement(it -> it.setType(Material.BOOKSHELF).setTitle("&3[&bShop&3]").setLore("&7Click to browse.").build());
									ed.setSlot(22);
									ed.setClick(click -> {
										click.setCancelled(true);
										ItemDemand.GUI.selectShopCategory(atm.getOwner().getUniqueId()).open(click.getElement());
									});
								});
								FillerElement<?> filler = new FillerElement<>(i);
								filler.add(ed -> {
									ed.setElement(it -> it.setType(Material.GRAY_STAINED_GLASS_PANE).setTitle(" ").build());
								});
								i.addItem(filler);
							})
							.orGet(m -> m instanceof SingularMenu && m.getKey().map(("main-panel-" + atm.id)::equals).orElse(false));
				case ACCOUNT_LOGIN:
					return MenuType.SINGULAR.build()
							.setTitle("Login (Place your card)")
							.setHost(plugin)
							.setSize(Menu.Rows.SIX)
							.setStock(i -> {
								i.addItem(ed -> {
									ed.setElement(it -> it.setType(Material.GLASS_PANE).setTitle(" ").build());
									ed.setSlot(22);
									ed.setClick(click -> {
										click.setCancelled(true);

										ItemStack item = click.getCursor();
										Message msg = Message.form(click.getElement()).setPrefix(RetroConomy.getInstance().getManager().getMain().getRoot().getString("Options.prefix"));
										if (DebitCard.matches(item)) {
											String id = item.getItemMeta().getPersistentDataContainer().get(DebitCard.KEY, PersistentDataType.STRING);

											RetroConomy.getInstance().getManager().getAccount(HUID.fromString(id)).ifPresent(a -> {
												if (a.getOwner().equals(click.getElement().getUniqueId()) || a.getJointOwner() != null && a.getJointOwner().equals(click.getElement().getUniqueId()) || a.getMembers().contains(click.getElement().getUniqueId())) {
													// access granted
													final ItemStack copy = item.clone();
													item.setAmount(0);
													click.getElement().getWorld().dropItem(click.getElement().getLocation(), copy);
													select(atm, a.getId(), Type.ACCOUNT).open(click.getElement());
												} else {
													click.getElement().closeInventory();
													// access denied
													Schedule.sync(() -> click.getElement().getWorld().playSound(atm.getLocation(), Sound.ENTITY_VILLAGER_NO, 10, 10)).cancelAfter(18).repeat(0, 20);
													msg.send("&cYou don't have access to this bank account.");
												}
											});
										} else {
											msg.send("&cThis debit card is invalid.");
											click.getElement().getWorld().playSound(click.getElement().getLocation(), Sound.ENTITY_VILLAGER_NO, 10, 1);
											click.getElement().closeInventory();
										}

									});
								});
								i.addItem(ed -> {
									ed.setElement(it -> it.setItem(SkullType.ARROW_BLACK_RIGHT.get()).setTitle("&7Place your debit card in the slot. &a&m→").build());
									ed.setSlot(20);
									ed.setType(ItemElement.ControlType.DISPLAY);
								});
								i.addItem(ed -> {
									ed.setElement(it -> it.setType(Material.PLAYER_HEAD).setTitle("&7▼ &3Forgot Card.").setLore("&7Enter your pin manually.").build());
									ed.setSlot(40);
									ed.setClick(click -> {
										click.setCancelled(true);
										write(atm, null, Type.FORGOT_CARD).open(click.getElement());
									});
								});
								FillerElement<?> filler = new FillerElement<>(i);
								filler.add(ed -> {
									ed.setElement(it -> it.setType(Material.BLUE_STAINED_GLASS_PANE).setTitle(" ").build());
								});
								i.addItem(filler);
							})
							.join();
				case ACCOUNT:
					return MenuType.SINGULAR.build()
							.setTitle("Account Options")
							.setKey("account-" + account.toString() + "-action")
							.setHost(plugin)
							.setSize(Menu.Rows.THREE)
							.setProperty(Menu.Property.CACHEABLE)
							.setStock(i -> {
								i.addItem(ed -> {
									ed.setElement(it -> it.setType(Material.GOLDEN_HELMET).setTitle("&6Withdraw").setLore("&7Click to transfer money to your wallet.").build());
									ed.setSlot(12);
									ed.setClick(click -> {
										click.setCancelled(true);
										write(atm, account, Type.WITHDRAW_ACCOUNT).open(click.getElement());
									});
								});
								i.addItem(ed -> {
									ed.setElement(it -> it.setType(Material.GOLDEN_HELMET).setTitle("&6Deposit").setLore("&7Click to transfer money to your account.").build());
									ed.setSlot(14);
									ed.setClick(click -> {
										click.setCancelled(true);
										write(atm, account, Type.DEPOSIT_ACCOUNT).open(click.getElement());
									});
								});
								i.addItem(ed -> {
									ed.setElement(it -> it.setType(Material.IRON_HELMET).setTitle("&2&m←&r &aChoose an option &2&m→").build());
									ed.setSlot(13);
									ed.setType(ItemElement.ControlType.DISPLAY);
								});
								FillerElement<?> filler = new FillerElement<>(i);
								filler.add(ed -> {
									ed.setElement(it -> it.setType(Material.GREEN_STAINED_GLASS_PANE).setTitle(" ").build());
								});
								i.addItem(filler);
							})
							.orGet(m -> m instanceof SingularMenu && m.getKey().map(("account-" + account + "-action")::equals).orElse(false));
				case WALLET:
					return MenuType.SINGULAR.build()
							.setTitle("Account Options")
							.setHost(plugin)
							.setSize(Menu.Rows.THREE)
							.setStock(i -> {
								i.addItem(ed -> {
									ed.setElement(it -> it.setType(Material.GOLDEN_HELMET).setTitle("&6Withdraw").setLore("&7Click to withdraw physical money.").build());
									ed.setSlot(12);
									ed.setClick(click -> {
										click.setCancelled(true);
										write(atm, account, Type.WITHDRAW_WALLET).open(click.getElement());
									});
								});
								i.addItem(ed -> {
									ed.setElement(it -> it.setType(Material.GOLDEN_HELMET).setTitle("&6Deposit").setLore("&7Click to deposit physical money.").build());
									ed.setSlot(14);
									ed.setClick(click -> {
										click.setCancelled(true);
										write(atm, account, Type.DEPOSIT_WALLET).open(click.getElement());
									});
								});
								i.addItem(ed -> {
									ed.setElement(it -> it.setType(Material.IRON_HELMET).setTitle("&2&m←&r &aChoose an option &2&m→").build());
									ed.setSlot(13);
									ed.setType(ItemElement.ControlType.DISPLAY);
								});
								FillerElement<?> filler = new FillerElement<>(i);
								filler.add(ed -> {
									ed.setElement(it -> it.setType(Material.GREEN_STAINED_GLASS_PANE).setTitle(" ").build());
								});
								i.addItem(filler);
							})
							.join();
				default:
					throw new IllegalStateException("");
			}
		}

		public enum Type {
			MAIN, ADMIN_PANEL, WALLET, ACCOUNT, ACCOUNT_LOGIN, FORGOT_CARD, TAX_EDIT, DEPOSIT_ACCOUNT, WITHDRAW_ACCOUNT, DEPOSIT_WALLET, WITHDRAW_WALLET
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

			Message msg = Message.form(p).setPrefix(RetroConomy.getInstance().getManager().getMain().getRoot().getString("Options.prefix"));

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

				Message msg = Message.form(p).setPrefix(RetroConomy.getInstance().getManager().getMain().getRoot().getString("Options.prefix"));
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
