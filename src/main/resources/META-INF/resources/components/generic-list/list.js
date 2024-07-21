customElements.define('custom-list', CustomList);

class CustomList extends HTMLElement {
    constructor() {
        super();
        this.attachShadow({ mode: 'open' });
    }

    connectedCallback() {
        this.render();
        this.setupEventListeners();
    }

    async render() {
        const type = this.getAttribute('type') || '';
        const skipColumns = this.getAttribute('skipColumns')?.split(' ') || [];
        const skipButtons = this.getAttribute('skipButtons')?.split(' ') || [];
        const parentId = this.getAttribute('parentId') || '';

        try {
            const response = await fetch(`/ui/list_js/${parentId}/${type}`);
            const html = await response.text();
            const parser = new DOMParser();
            const doc = parser.parseFromString(html, 'text/html');
            const items = Array.from(doc.querySelectorAll('li'));

            let listHtml = '<ul>';
            items.forEach(item => {
                const id = item.getAttribute('id');
                listHtml += `<li data-id="${id}">`;
                Array.from(item.children).forEach(child => {
                    if (!skipColumns.includes(child.getAttribute('class'))) {
                        listHtml += child.outerHTML;
                    }
                });
                listHtml += '</li>';
            });
            listHtml += '</ul>';

            let actionBarHtml = '<div class="action-bar">';
            if (!skipButtons.includes('create')) {
                actionBarHtml += `<button class="create">Create</button>`;
            }
            if (!skipButtons.includes('edit')) {
                actionBarHtml += `<button class="edit">Edit</button>`;
            }
            if (!skipButtons.includes('remove')) {
                actionBarHtml += `<button class="remove">Remove</button>`;
            }
            actionBarHtml += '</div>';

            this.shadowRoot.innerHTML = `${listHtml}${actionBarHtml}`;
        } catch (error) {
            console.error('Error fetching list:', error);
            this.shadowRoot.innerHTML = '<p>Error loading list</p>';
        }
    }

    setupEventListeners() {
        const list = this.shadowRoot.querySelector('ul');
        const actionBar = this.shadowRoot.querySelector('.action-bar');
        const type = this.getAttribute('type') || '';
        const parentId = this.getAttribute('parentId') || '';

        list.addEventListener('click', (e) => {
            const li = e.target.closest('li');
            if (li) {
                list.querySelectorAll('li').forEach(item => item.classList.remove('selected'));
                li.classList.add('selected');
            }
        });

        actionBar.addEventListener('click', async (e) => {
            if (e.target.matches('button')) {
                const action = e.target.classList[0];
                const selectedItem = list.querySelector('li.selected');
                const id = selectedItem ? selectedItem.dataset.id : '';

                let url = `/${type}/${action}`;
                if (action !== 'create' && id) {
                    url += `/${id}`;
                }

                url += `?parentId=${parentId}`;

                try {
                    const response = await fetch(url, { method: 'GET' });
                    if (!response.ok) {
                        throw new Error(`HTTP error! status: ${response.status}`);
                    }
                    console.log(`${action} action completed successfully`);
                    // You might want to refresh the list or perform other actions here
                    this.render();
                } catch (error) {
                    console.error(`Error performing ${action} action:`, error);
                }
            }
        });
    }
}
