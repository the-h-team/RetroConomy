/*
 *  Copyright 2021 Sanctum <https://github.com/the-h-team>
 *
 *  This file is part of RetroConomy.
 *
 *  This software is currently in development and its licensing has not
 *  yet been chosen.
 */
package com.github.sanctum.retro.construct.core;

import com.github.sanctum.labyrinth.library.HUID;
import com.github.sanctum.retro.RetroConomy;
import java.io.Serializable;
import java.math.BigDecimal;
import org.bukkit.World;

public interface RetroAccount extends Serializable {

	HUID getId();

	BigDecimal getBalance();

	BigDecimal getBalance(World world);

	boolean has(double amount);

	boolean has(double amount, World world);

	boolean has(BigDecimal amount);

	boolean has(BigDecimal amount, World world);

	RetroConomy.TransactionResult setBalance(double amount);

	RetroConomy.TransactionResult setBalance(BigDecimal amount);

	RetroConomy.TransactionResult setBalance(double amount, World world);

	RetroConomy.TransactionResult setBalance(BigDecimal amount, World world);

	RetroConomy.TransactionResult deposit(BigDecimal amount);

	RetroConomy.TransactionResult deposit(BigDecimal amount, World world);

	RetroConomy.TransactionResult withdraw(BigDecimal amount);

	RetroConomy.TransactionResult withdraw(BigDecimal amount, World world);

}
