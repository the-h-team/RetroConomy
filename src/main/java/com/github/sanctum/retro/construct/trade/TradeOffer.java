package com.github.sanctum.retro.construct.trade;

import com.github.sanctum.retro.construct.trade.exceptions.InvalidTradeOfferException;
import com.github.sanctum.retro.construct.trade.exceptions.TraderException;
import com.github.sanctum.retro.construct.trade.exceptions.TraderItemException;
import com.github.sanctum.retro.construct.trade.exceptions.TraderMoneyException;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

/**
 * Base class for trade offers.
 * <p>
 * See internal implementations:
 * <pre>
 *     {@link Item}
 *     {@link Items}
 *     {@link Money}
 * </pre>
 */
public abstract class TradeOffer {
    /**
     * Get a dialog-friendly description of the offer.
     * <p>
     * "One Green Wool" for instance, or "$10"
     *
     * @return a dialog-friendly description
     */
    public abstract @NotNull String getDescription();

    /**
     * Give this offer to a trader.
     *
     * @param trader a trader
     * @throws InvalidTradeOfferException if the trader does not support this
     * offer type
     * @throws TraderException if an error occurs performing the trade
     */
    protected abstract void give(Trader trader) throws InvalidTradeOfferException, TraderException;

    /**
     * Take this offer from a trader.
     *
     * @param trader a trader
     * @throws InvalidTradeOfferException if the trader does not support this
     * offer type
     * @throws TraderException if an error occurs performing the trade
     */
    protected abstract void take(Trader trader) throws InvalidTradeOfferException, TraderException;

    public abstract static class Item extends TradeOffer {
        public abstract @NotNull ItemStack getItem();

        @Override
        public @NotNull String getDescription() {
            return Items.getItemName(getItem());
        }

        @Override
        protected void give(Trader trader) throws InvalidTradeOfferException, TraderItemException {
            if (trader instanceof ItemTrader) {
                ((ItemTrader) trader).giveItem(getItem());
            }
            throw new InvalidTradeOfferException("This trader cannot accept " + getDescription());
        }

        @Override
        protected void take(Trader trader) throws InvalidTradeOfferException, TraderItemException {
            if (trader instanceof ItemTrader) {
                ((ItemTrader) trader).takeItem(getItem());
            }
            throw new InvalidTradeOfferException("Cannot remove " + getDescription() + " from this trader");
        }
    }

    public abstract static class Items extends TradeOffer {
        public abstract @NotNull List<@NotNull ItemStack> getItems();

        @Override
        public @NotNull String getDescription() {
            final List<@NotNull ItemStack> items = getItems();
            final int size = items.size();
            if (size == 1) {
                return getItemName(items.get(0));
            }
            return size + " items";
        }

        protected static String getItemName(@NotNull ItemStack item) {
            final int amount = item.getAmount();
            if (item.hasItemMeta()) {
                final ItemMeta itemMeta = item.getItemMeta();
                if (itemMeta.hasDisplayName()) {
                    final String displayName = itemMeta.getDisplayName();
                    if (amount != 1) {
                        return String.valueOf(amount) + ' ' + displayName;
                    }
                    return displayName;
                }
            }
            final String materialName = item.getType().name().toLowerCase(Locale.ROOT);
            if (amount != 1) {
                return String.valueOf(amount) + ' ' + materialName + 's';
            }
            return 1 + materialName;
        }

        @Override
        protected void give(Trader trader) throws InvalidTradeOfferException, TraderItemException {
            if (trader instanceof ItemTrader) {
                ((ItemTrader) trader).giveItems(getItems());
            }
            throw new InvalidTradeOfferException("This trader cannot accept " + getDescription());
        }

        @Override
        protected void take(Trader trader) throws InvalidTradeOfferException, TraderItemException {
            if (trader instanceof ItemTrader) {
                ((ItemTrader) trader).takeItems(getItems());
            }
            throw new InvalidTradeOfferException("Cannot remove " + getDescription() + " from this trader");
        }
    }

    public static class Money extends TradeOffer {
        private final BigDecimal amount;

        public Money(@NotNull BigDecimal amount) {
            this.amount = amount;
        }

        public final @NotNull BigDecimal getAmount() {
            return amount;
        }

        @Override
        public @NotNull String getDescription() {
            return formatAmount();
        }

        @Override
        protected void give(Trader trader) throws InvalidTradeOfferException, TraderMoneyException {
            if (trader instanceof MoneyTrader) {
                ((MoneyTrader) trader).giveMoney(amount);
            }
            throw new InvalidTradeOfferException("Unable to give " + formatAmount() + " to this trader");
        }

        @Override
        protected void take(Trader trader) throws InvalidTradeOfferException, TraderMoneyException {
            if (trader instanceof MoneyTrader) {
                ((MoneyTrader) trader).takeMoney(amount);
            }
            throw new InvalidTradeOfferException("Unable to give " + formatAmount() + " to this trader");
        }

        protected @NotNull String formatAmount() {
            return "$" + amount;
        }
    }
}
