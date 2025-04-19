package chadlymasterson.safepastures.mixin;

import chadlymasterson.safepastures.ConfigLoader;
import chadlymasterson.safepastures.SafePastures;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(PokemonEntity.class)
public class PokemonEntityMixin {

    public PokemonEntityMixin() {
        super();
    }

    @Inject(method="damage", at = @At("HEAD"), cancellable = true)
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        PokemonEntity self = (PokemonEntity)(Object) this;
        if(!self.getWorld().isClient()) {
            ConfigLoader config = SafePastures.getConfig((ServerWorld) self.getWorld());

            if(self.getTethering() != null) handleDamage(source, amount, cir, config);
        }
    }

    private void handleDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir, ConfigLoader config) {
        if(!config.preventPokemonDamageInPasture) {
            cir.setReturnValue(true);
            return;
        };
        var takeDamage = false;

        if(config.useBlackList) {
            for(Map.Entry<String, Boolean> entry : config.damageSourceBlackList.entrySet()) {
                if ((entry.getKey().equals(source.getName()) && entry.getValue())) {
                    takeDamage = true;
                    break;
                }
            }
        }

        if(!takeDamage) handleDamageEffects(source);

        cir.setReturnValue(takeDamage);
    }

    private void handleDamageEffects(DamageSource source) {
        LivingEntity self = ((LivingEntity) (Object) this);

        if (self.isOnFire()) self.extinguish();

        self.clearStatusEffects();

        if (self.getAir() < self.getMaxAir()) self.setAir(self.getMaxAir());
        if (self.getFrozenTicks() > 0) self.setFrozenTicks(0);
    }

    private boolean isInPasture(LivingEntity self) {
        return self.getWorld().getRegistryKey().getValue().getPath().contains("pasture");
    }
}
