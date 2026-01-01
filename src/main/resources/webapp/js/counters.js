// SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
// SPDX-License-Identifier: MIT

/**
 * stateful.co
 *
 * This source file is subject to the new BSD license that is bundled
 * with this package in the file LICENSE.txt. It is also available
 * through the world-wide-web at this URL: http://www.stateful.co/LICENSE.txt
 * If you did not receive a copy of the license and are unable to
 * obtain it through the world-wide-web, please send an email
 * to license@stateful.co so we can send you a copy immediately.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 * @copyright Copyright (c) stateful.co, 2010-2012
 */

/*globals $: false, document: false */

$(document).ready(
    function () {
        function input($this) {
            return $this.closest('.counter').find('input');
        }
        function increment($input, value) {
            $.ajax(
                {
                    url: $input.attr('data-href-increment') + '?value=' + value,
                    success: function (data) {
                        $input.val(data);
                    }
                }
            );
        }
        $('.refresh').click(
            function () {
                increment(input($(this)), 0);
            }
        );
        $('.increment').click(
            function () {
                increment(input($(this)), 1);
            }
        );
        $('.save').click(
            function () {
                var $input = input($(this));
                $.ajax(
                    {
                        type: 'PUT',
                        url: $input.attr('data-href-set') + '?value=' + $input.val(),
                        success: function (data) {
                            increment($input, 0);
                        }
                    }
                );
            }
        );
    }
);
