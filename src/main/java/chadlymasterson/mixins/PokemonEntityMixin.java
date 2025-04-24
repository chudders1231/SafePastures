package chadlymasterson.mixins;

import chadlymasterson.safepastures.ConfigLoader;
import chadlymasterson.safepastures.SafePastures;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
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

    @Inject(method="hurt", at = @At("HEAD"), cancellable = true)
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        PokemonEntity self = (PokemonEntity)(Object) this;

        if(!self.level().isClientSide) {
            ConfigLoader config = SafePastures.getConfig((ServerLevel) self.level());

            if(self.getTethering() != null) handleDamage(source, amount, cir, config);
        }
    }

    private void handleDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir, ConfigLoader config) {
        if(!config.preventPokemonDamageInPasture) {
            cir.setReturnValue(true);
            return;
        }

        Boolean takeDamage = false;

        if(config.useBlackList) {
            for(Map.Entry<String, Boolean> entry : config.damageSourceBlackList.entrySet()) {
                if (entry.getKey().equals(source.type().toString()) && entry.getValue()) {
                    takeDamage = true;
                    return;
                }
            }
        }

        if(!takeDamage) handleDamageEffects(source);
        cir.setReturnValue(takeDamage);
    }

    private void handleDamageEffects(DamageSource source) {
        LivingEntity self = ((LivingEntity) (Object) this);

        if (self.isOnFire()) self.extinguishFire();

        self.removeAllEffects();

        if(self.getAirSupply() < self.getMaxAirSupply()) self.setAirSupply(self.getMaxAirSupply());
        if (self.getTicksFrozen() > 0) self.setTicksFrozen(0);
    }
}
