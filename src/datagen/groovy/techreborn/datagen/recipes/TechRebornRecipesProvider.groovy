/*
 * This file is part of TechReborn, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2020 TechReborn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package techreborn.datagen.recipes

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipesProvider
import net.minecraft.advancement.criterion.CriterionConditions
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.item.ItemConvertible
import net.minecraft.recipe.Ingredient
import net.minecraft.tag.Tag
import net.minecraft.util.Identifier
import techreborn.datagen.recipes.machine.MachineRecipeJsonFactory

import techreborn.init.ModRecipes

import java.util.function.Consumer

abstract class TechRebornRecipesProvider extends FabricRecipesProvider {
    protected Consumer<RecipeJsonProvider> exporter
    TechRebornRecipesProvider(FabricDataGenerator dataGenerator) {
        super(dataGenerator)
    }

    @Override
    protected void generateRecipes(Consumer<RecipeJsonProvider> exporter) {
        this.exporter = exporter
        generateRecipes()
    }

    abstract void generateRecipes()

    static Ingredient createIngredient(def input) {
        if (input instanceof Ingredient) {
			return input
		}
		if (input instanceof ItemConvertible) {
            return Ingredient.ofItems(input)
        } else if (input instanceof Tag.Identified) {
            return Ingredient.fromTag(input)
        }

        throw new IllegalArgumentException()
    }

    static String getCriterionName(def input) {
        if (input instanceof ItemConvertible) {
            return hasItem(input)
        } else if (input instanceof Tag.Identified) {
            return "has_tag_" + input.getId()
        }

        throw new IllegalArgumentException()
    }

    static CriterionConditions getCriterionConditions(def input) {
        if (input instanceof ItemConvertible) {
            return conditionsFromItem(input)
        } else if (input instanceof Tag.Identified) {
            return conditionsFromTag(input)
        }

        throw new IllegalArgumentException()
    }

    static String getInputPath(def input) {
        if (input instanceof ItemConvertible) {
            return getItemPath(input)
        } else if (input instanceof Tag.Identified) {
            return input.getId().toString().replace(":", "_")
        }

        throw new IllegalArgumentException()
    }

    static String getName(def input) {
        if (input instanceof ItemConvertible) {
			return getItemPath(input)
        } else if (input instanceof Tag.Identified) {
			String name = input.getId().toString()
            if (name.contains(":"))
                name = name.substring(name.indexOf(":")+1)
			return name
        }

        throw new IllegalArgumentException()
    }

	static String getNamePart1(def input) {
		String name
		if (input instanceof ItemConvertible) {
			name = getItemPath(input)
			return name.substring(0,name.indexOf("_"))
		} else if (input instanceof Tag.Identified) {
			name = input.getId().toString()
			if (name.contains(":"))
				name = name.substring(name.indexOf(":")+1)
			return name.substring(0,name.indexOf("_"))
		}

		throw new IllegalArgumentException()
	}

	def offerGrinderRecipe(@DelegatesTo(value = MachineRecipeJsonFactory.class, strategy = Closure.DELEGATE_FIRST) Closure closure) {
		MachineRecipeJsonFactory.create(ModRecipes.GRINDER, closure).offerTo(exporter)
	}

    @Override
    protected Identifier getRecipeIdentifier(Identifier identifier) {
        return new Identifier("techreborn", super.getRecipeIdentifier(identifier).path)
    }
}
