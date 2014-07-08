/**
 * Plugabble.java
 *
 * Copyright (C) 2014
 *
 * Anderson de Oliveira Antunes <anderson.utf@gmail.com>
 *
 * This file is part of S3F.
 *
 * S3F is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * S3F is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * S3F. If not, see http://www.gnu.org/licenses/.
 */
package s3f.core.plugin;

/**
 *
 *
 *
 * @author Anderson de Oliveira Antunes <anderson.utf@gmail.com>
 */
public interface Plugabble extends Configurable {

    /**
     * Transforma esta factory em um objeto funcional.
     */
    public void init();

    /**
     * Cria uma nova instancia, devidamente incializada, deste objeto.
     *
     * Invocar esse metodo é valido apenas para componentes não Singleton. É
     * importante ressaltar que a nova instancia deve ser registrada como filha
     * de alguma outra ou ser registrada como instância de base em
     * {@link PluginManager}.
     *
     * @return
     */
    public Plugabble createInstance(); //FIXME: remover esse metodo e substituir por um que cria um objeto usando generics e registra automaticamente ele!
}
