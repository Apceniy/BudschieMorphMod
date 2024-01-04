package de.budschie.bmorph.capabilities;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import de.budschie.bmorph.capabilities.MorphStateMachine.MorphStateMachineChangeRecorder;
import de.budschie.bmorph.capabilities.MorphStateMachine.MorphStateMachineRecordedChanges;
import de.budschie.bmorph.morph.FavouriteList;
import de.budschie.bmorph.morph.MorphItem;
import de.budschie.bmorph.morph.MorphList;
import de.budschie.bmorph.morph.MorphReason;
import de.budschie.bmorph.morph.MorphReasonRegistry;
import de.budschie.bmorph.morph.functionality.Ability;
import de.budschie.bmorph.network.MorphStateMachineChangedSync.MorphStateMachineChangedSyncPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public interface IMorphCapability
{
	public Player getOwner();
	
	/**
	 * Returns the {@link AbilitySerializationContext} which is used in
	 * {@link IMorphCapability#serializeSavableAbilityData()} and
	 * {@link IMorphCapability#deserializeSavableAbilityData(CompoundTag)} to save
	 * and load ability data.
	 **/
	public AbilitySerializationContext getAbilitySerializationContext();
	
	/** Getter for the value returned in {@link IMorphCapability#getAbilitySerializationContext()} **/
	public void setAbilitySerializationContext(AbilitySerializationContext context);
		
	/** Returns the morph that this player is currently morphed into. **/
	public Optional<MorphItem> getCurrentMorph();
	/** Returns the entity which the morph that this player is currently morphed into represents. **/
	public Optional<Entity> getCurrentMorphEntity();
	
	/** This method adds the given morph item to the morph list. **/
	public void addMorphItem(MorphItem morphItem);
	/** Removes a given morph item from the morph list. **/
	public void removeMorphItem(MorphItem morphItem);
	/** Removes a given morph associated with the given key from the morph list. **/
	public void removeMorphItem(UUID key);
	/** This method returns the morph list as an object. **/
	public MorphList getMorphList();
	/** This is a setter for the morph list. **/
	public void setMorphList(MorphList list);
	/** Retrieves the MorphStateMachine. It can be used by abilities to implement complex FSM behaviour. **/
	public MorphStateMachine getMorphStateMachine();
	/** Sets the morph state machine. **/
	public void setMorphStateMachine(MorphStateMachine morphStateMachine);
	/** Starts recording MorphStateMachine changes. **/
	public MorphStateMachineChangeRecorder createMorphStateMachineChangeRecorder();
	/** Creates recorded changes from a MorphStateMachineChangedSyncPacket. **/
	public MorphStateMachineRecordedChanges createRecordedChangesFromPacket(MorphStateMachineChangedSyncPacket packet);

	public Optional<EntityDimensions> getOverrideEntityDimensions();
	public void setOverrideEntityDimensions(Optional<EntityDimensions> overrideEntityDimensions);

	/**
	 * @return whether the player in question is a ghost and should therefor not be attacked by mobs
	 * in their surrounding whatsoever.
	 */
	public boolean isGhost();

	/**
	 * @param value New value for {@link IMorphCapability#isGhost()}. Cannot be synced to the client.
	 */
	public void setGhost(boolean value);
	
	/** This sets the morph item, and its value can be retrieved by invoking {@link IMorphCapability#getCurrentMorph()}. **/
	public void setMorph(MorphItem morph, MorphReason reason);
	
	/**
	 * Returns the reason why this player morphed. You can find a list of default reasons in {@link MorphReasonRegistry}
	 */
	public MorphReason getMorphReason();
		
	/**
	 * The purpose of this method is to clear the Optionals holding the current
	 * morph data. {@link IMorphCapability#getCurrentMorph()} will return an empty
	 * optional.
	 **/
	public void demorph(MorphReason reason);
	
	/**
	 * Sets the morph reason. Only use this method for cap serializing purposes.
	 */
	public void setMorphReason(MorphReason reason);
	
	public void applyHealthOnPlayer();
	
	/**
	 * By calling this method, you sync the capability data with every player that is tracking this player.
	 * This method shall not be called if you intent to try to synchronize a morph
	 * change across every client. Use
	 * {@link IMorphCapability#syncMorphChange(PlayerEntity)} to do this.
	 **/
	public void syncWithClients();
	
	/** This method is used to synchronize this capability with a specific target. **/
	public void syncWithClient(ServerPlayer syncTo);
	
	/** This method is much like the method described above, just with an network manager as a target instead of a player as a target. **/
	public void syncWithConnection(Connection connection);
	
	/** Syncs the changes which have been recorded to the MorphStateMachine to the clients around this player. **/
	public void syncMorphStateMachineRecordedChanges(MorphStateMachineRecordedChanges recordedChanges);
	/** This method synchronizes a morph change to all players. **/
	public void syncMorphChange();
	/** This method synchronizes the acquisition of a morph to all players. **/
	public void syncMorphAcquisition(MorphItem item);
	/** This method synchronizes the removal of a morph to all tracking players. **/
	public void syncMorphRemoval(UUID... morphItemKeys);
	
	/** This method syncs the addition of one or more abilities. **/
	public void syncAbilityAddition(Ability...abilities);
	
	/** This method syncs the removal of one or more abilities. **/
	public void syncAbilityRemoval(Ability...abilities);
	
	/** Returns the value of the flag mentioned in {@link IMorphCapability#setMobAttack(boolean)}. **/
	public boolean shouldMobsAttack();
	
	/** This method is a flag that indicates whether the mob attack ability is present or not. Note that this value defaults to {@code false}. **/
	public void setMobAttack(boolean value);
	
	@Nullable
	/** This list returns all currently active abilities. It may be null. **/
	public List<Ability> getCurrentAbilities();
	
	/** This is simply a setter for all current abilities. **/
	public void setCurrentAbilities(List<Ability> abilities);
	
	/** This applies abilities, meaning that we iterate over the list of abilities and call the apply method on them. **/
	public void applyAbilities(MorphItem oldMorphItem, List<Ability> oldAbilities);
	/** This method deapplies all abilities by once again iterating over every old ability and deapplying it. **/
	public void deapplyAbilities(MorphItem aboutToMorphTo, List<Ability> newAbilities);
	
	/** This method adds a single ability to the list of abilities and enables it. **/
	public void applyAbility(Ability ability);
	
	/** This method searches for the given ability and removes + deapplies it when it was found. **/
	public void deapplyAbility(Ability ability);
	
	/** This will iterate over every ability and signal them that the button to use an ability has been pressed. **/
	public void useAbility();
	
	/** Iterates over every current ability and removes references to the player. **/
	public void removePlayerReferences();
	
	/**
	 * Calling this method will iterate over every currently active ability and try
	 * to serialize data of said ability.
	 * 
	 * @return A compound that can be saved to disk.
	 **/	
	public CompoundTag serializeSavableAbilityData();
	
	/**
	 * Calling this method will load all savable ability data.
	 */
	public void deserializeSavableAbilityData(CompoundTag compoundTag);
	
	// Aggro timestamps are measured in ints. Aggro timestamp => not saved, aggro duration => saved (indicates how long mobs will be aggro)
	public int getLastAggroTimestamp();
	public void setLastAggroTimestamp(int timestamp);
	public int getLastAggroDuration();
	public void setLastAggroDuration(int aggroDuration);
	
	/** This method is a getter for the morph favourite list. **/
	public FavouriteList getFavouriteList();
	public void setFavouriteList(FavouriteList favouriteList);
}
