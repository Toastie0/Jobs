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
            LOGGER.info("Attempting to initialize Impactor economy integration...");
            
            economyService = Impactor.instance().services().provide(EconomyService.class);
            if (economyService == null) {
                LOGGER.error("EconomyService is null - Impactor may not be loaded correctly!");
                return;
            }
            
            primaryCurrency = economyService.currencies().primary();
            if (primaryCurrency == null) {
                LOGGER.error("Primary currency is null - Impactor economy not configured properly!");
                return;
            }
            
            LOGGER.info("✓ Successfully initialized Impactor economy!");
            LOGGER.info("  Currency: {}", primaryCurrency.key().asString());
            LOGGER.info("  Economy rewards will now work correctly.");
        } catch (Exception e) {
            LOGGER.error("✗ Failed to initialize Impactor economy!", e);
            LOGGER.error("  Make sure Impactor mod is installed and loaded before this mod.");
            LOGGER.error("  Economy rewards will NOT work until this is fixed!");
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
            LOGGER.error("Cannot deposit money - economy service not initialized!");
            LOGGER.error("  EconomyService: {}, PrimaryCurrency: {}", economyService, primaryCurrency);
            return CompletableFuture.completedFuture(false);
        }
        
        LOGGER.debug("Attempting to deposit ${} to player {}", amount, uuid);
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        
        economyService.account(primaryCurrency, uuid).thenAccept(account -> {
            try {
                BigDecimal depositAmount = BigDecimal.valueOf(amount);
                account.deposit(depositAmount);
                
                LOGGER.debug("Successfully deposited ${} to player {} (new balance: ${})", 
                    amount, uuid, account.balance());
                future.complete(true);
            } catch (Exception e) {
                LOGGER.error("Failed to deposit ${} to player {} - Error: {}", amount, uuid, e.getMessage(), e);
                future.complete(false);
            }
        }).exceptionally(throwable -> {
            LOGGER.error("Failed to get account for player {} - Error: {}", uuid, throwable.getMessage(), throwable);
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
            LOGGER.error("Cannot withdraw money - economy service not initialized!");
            return CompletableFuture.completedFuture(false);
        }
        
        LOGGER.debug("Attempting to withdraw ${} from player {}", amount, uuid);
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        
        economyService.account(primaryCurrency, uuid).thenAccept(account -> {
            try {
                BigDecimal withdrawAmount = BigDecimal.valueOf(amount);
                
                // Check if player has enough balance
                if (account.balance().compareTo(withdrawAmount) >= 0) {
                    account.withdraw(withdrawAmount);
                    LOGGER.debug("Successfully withdrew ${} from player {} (new balance: ${})", 
                        amount, uuid, account.balance());
                    future.complete(true);
                } else {
                    LOGGER.debug("Player {} has insufficient funds for withdrawal of ${} (balance: ${})", 
                        uuid, amount, account.balance());
                    future.complete(false);
                }
            } catch (Exception e) {
                LOGGER.error("Failed to withdraw ${} from player {} - Error: {}", amount, uuid, e.getMessage(), e);
                future.complete(false);
            }
        }).exceptionally(throwable -> {
            LOGGER.error("Failed to get account for player {} - Error: {}", uuid, throwable.getMessage(), throwable);
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
