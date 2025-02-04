package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.MajruszsEnchantments;
import com.majruszsenchantments.common.Handler;
import com.mlib.annotation.AutoInstance;
import com.mlib.contexts.OnEntityPreDamaged;
import com.mlib.entity.EntityHelper;
import com.mlib.item.CustomEnchantment;
import com.mlib.item.EnchantmentHelper;
import com.mlib.item.EquipmentSlots;
import com.mlib.math.Range;
import net.minecraft.world.item.enchantment.Enchantment;

@AutoInstance
public class DeathWishEnchantment extends Handler {
	Range< Float > damage = Range.of( 1.0f, 2.0f );
	Range< Float > vulnerability = Range.of( 0.7f, 1.2f );

	public static CustomEnchantment create() {
		return new CustomEnchantment()
			.rarity( Enchantment.Rarity.RARE )
			.category( MajruszsEnchantments.IS_MELEE )
			.slots( EquipmentSlots.MAINHAND )
			.minLevelCost( level->12 )
			.maxLevelCost( level->50 );
	}

	public DeathWishEnchantment() {
		super( MajruszsEnchantments.DEATH_WISH, false );

		OnEntityPreDamaged.listen( this::increaseDamageDealt )
			.addCondition( data->data.attacker != null )
			.addCondition( data->EnchantmentHelper.has( this.enchantment, data.attacker ) );

		OnEntityPreDamaged.listen( this::increaseDamageReceived )
			.addCondition( data->EnchantmentHelper.has( this.enchantment, data.target ) );

		this.config.defineFloatRange( "damage_multiplier_range", s->this.damage, ( s, v )->this.damage = Range.of( 0.0f, 10.0f ).clamp( v ) );
		this.config.defineFloatRange( "vulnerability_multiplier_range", s->this.vulnerability, ( s, v )->this.vulnerability = Range.of( 0.0f, 10.0f ).clamp( v ) );
	}

	private void increaseDamageDealt( OnEntityPreDamaged data ) {
		float multiplier = this.damage.lerp( ( float )EntityHelper.getMissingHealthRatio( data.attacker ) );

		data.damage *= multiplier;
		if( multiplier > 1.0f ) {
			data.spawnMagicParticles = true;
		}
	}

	private void increaseDamageReceived( OnEntityPreDamaged data ) {
		data.damage *= this.vulnerability.lerp( ( float )EntityHelper.getHealthRatio( data.target ) );
	}
}
