
/**
 * @description A list which GETs it's content server-side upon load.
 * 
 * @att endpoint string Specifies the endpoint for the GET request generating the list content.
 */
class ProvisionerList extends HTMLElement {
	constructor() {
		super();
		const endpoint = this.getAttribute('endpoint');
		this.attachShadow({ mode: 'open' });
		this.shadowRoot.innerHTML = `
			<div class="frame">
				<div hx-get="${endpoint}" hx-trigger="load">${endpoint ? "@endpoint is missing." : ""}</div>
			</div>`;
	}
}

customElements.define('prov-list', ProvisionerList);