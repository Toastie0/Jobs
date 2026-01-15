package net.toastie.jobs.economy;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.economy.EconomyService;
import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.currency.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Handles integration with Impactor Economy API.
 * Provides methods for depositing and withdrawing money from player accounts.
 */
public class ImpactorEconomyHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImpactorEconomyHandler.class);
    
    private EconomyService economyService;
    private Currency primaryCurrency;
    
    /**
     * Initializes the economy handler and gets the primary currency.
     */
    public void initialize() {
        try {
            economyService = Impactor.instance().services().provide(EconomyService.class);
            primaryCurrency = economyService.currencies().primary();
            
            LOGGER.info("Initialized Impactor economy with currency: {}", primaryCurrency.key().asString());
        } catch (Exception e) {
            LOGGER.error("Failed to initialize Impactor economy", e);
        }
    }
    
    /**
     * Deposits money into a player's account.
     * 
     * @param uuid Player UUID
     * @param amount Amount to deposit
     * @return CompletableFuture that completes when the transaction is done
     */
    public CompletableFuture<Boolean> depositMoney(UUID uuid, double amount) {
        if (economyService == null || primaryCurrency == null) {
            LOGGER.error("Economy service not initialized");
            return CompletableFuture.completedFuture(false);
        }
        
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        
        economyService.account(primaryCurrency, uuid).thenAccept(account -> {
            try {
                BigDecimal depositAmount = BigDecimal.valueOf(amount);
                account.deposit(depositAmount);
                
                LOGGER.debug("Deposited {} to player {}", amount, uuid);
                future.complete(true);
            } catch (Exception e) {
                LOGGER.error("Failed to deposit money to player {}", uuid, e);
                future.complete(false);
            }
        }).exceptionally(throwable -> {
            LOGGER.error("Failed to get account for player {}", uuid, throwable);
            future.complete(false);
            return null;
        });
        
        return future;
    }
    
    /**
     * Withdraws money from a player's account.
     * 
     * @param uuid Player UUID
     * @param amount Amount to withdraw
     * @return CompletableFuture that completes with true if successful, false if insufficient funds
     */
    public CompletableFuture<Boolean> withdrawMoney(UUID uuid, double amount) {
        if (economyService == null || primaryCurrency == null) {
            LOGGER.error("Economy service not initialized");
            return CompletableFuture.completedFuture(false);
        }
        
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        
        economyService.account(primaryCurrency, uuid).thenAccept(account -> {
            try {
                BigDecimal withdrawAmount = BigDecimal.valueOf(amount);
                
                // Check if player has enough balance
                if (account.balance().compareTo(withdrawAmount) >= 0) {
                    account.withdraw(withdrawAmount);
                    LOGGER.debug("Withdrew {} from player {}", amount, uuid);
                    future.complete(true);
                } else {
                    LOGGER.debug("Player {} has insufficient funds for withdrawal of {}", uuid, amount);
                    future.complete(false);
                }
            } catch (Exception e) {
                LOGGER.error("Failed to withdraw money from player {}", uuid, e);
                future.complete(false);
            }
        }).exceptionally(throwable -> {
            LOGGER.error("Failed to get account for player {}", uuid, throwable);
            future.complete(false);
            return null;
        });
        
        return future;
    }
    
    /**
     * Checks if the economy service is available.
     */
    public boolean isAvailable() {
        return economyService != null && primaryCurrency != null;
    }
}
