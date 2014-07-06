/**
 * System.java
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
package s3f.base.simulation;

public interface System {

    public static final int PAUSED = 0;
    public static final int RUNNING = 1;

    public void setSystemState(int state);

    public int getSystemState();

    /**
     * Utilizado para atualiazar algumas variaveis antes de começar um novo
     * ciclo.
     */
    public void beginStep();

    /**
     * Executa um passo local. Restornar false indica que este sistema não está
     * pronto para um novo passo global, retornar true indica que um novo passo
     * global pode ser iniciado.
     *
     * @return retorna false para continuar, e true para sair, do ciclo global
     */
    public boolean performStep();
}
