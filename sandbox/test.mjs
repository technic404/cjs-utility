
class CjsComponent {
    /**
     * @param {object} data
     * @returns {this}
     */
    withData(data) {
        console.log(data)
    }
}

/**
 * @typedef {Object} MyUser
 * @property {number} age
 * @property {string} namme
 */

/** @cjs {MyUser} */
export const Component = new CjsComponent();

