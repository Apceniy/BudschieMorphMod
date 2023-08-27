package de.budschie.bmorph.morph.functionality.configurable;

import java.awt.TextComponent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import de.budschie.bmorph.morph.functionality.Ability;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class InstaDeathOnCookieAbility extends Ability
{
	public static final Codec<InstaDeathOnCookieAbility> CODEC = RecordCodecBuilder.create(instance -> instance
			.group(Codec.BOOL.optionalFieldOf("painful", false).forGetter(InstaDeathOnCookieAbility::isPainfulDeath))
			.apply(instance, InstaDeathOnCookieAbility::new));
	
	private boolean isPainfulDeath;
	
	public InstaDeathOnCookieAbility(boolean isPainfulDeath)
	{
		this.isPainfulDeath = isPainfulDeath;
	}
	
	public boolean isPainfulDeath()
	{
		return isPainfulDeath;
	}
	
	@SubscribeEvent
	public void onEat(LivingEntityUseItemEvent.Finish event)
	{
		if(isTracked(event.getEntity()))
		{
			Player player = (Player) event.getEntity();
			
			if(event.getItem().getItem() == Items.COOKIE)
			{
				if(isPainfulDeath)
				{
					// JEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEESSSSSSSSSSSSSSSSSSSSSSSSSSSSSS u found easteregg
					player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100000, 10));
					player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100000, 10));
					player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 10000, 100));
					player.addEffect(new MobEffectInstance(MobEffects.POISON, 1000, 2));
					player.addEffect(new MobEffectInstance(MobEffects.WITHER, 1000, 0));
					player.setGlowingTag(true);
					player.setDeltaMovement(0, 10, 0);
					
					player.sendSystemMessage(MutableComponent.create(new LiteralContents("H4h4 u d3d s00n")).withStyle(ChatFormatting.RED));
				}
				else
				{
					player.hurt(player.level.damageSources().badRespawnPointExplosion(player.position()), 420000000);
					
					if(player.isDeadOrDying())
						player.sendSystemMessage(MutableComponent.create(new LiteralContents("I have told you several times that you should not eat cookies.")).withStyle(ChatFormatting.RED));
					else
						player.sendSystemMessage(MutableComponent.create(new LiteralContents("How the.... what? You survived?")).withStyle(ChatFormatting.RED));
				}
			}
		}
	}
	
	@Override
	public boolean isAbleToReceiveEvents()
	{
		return true;
	}
}
